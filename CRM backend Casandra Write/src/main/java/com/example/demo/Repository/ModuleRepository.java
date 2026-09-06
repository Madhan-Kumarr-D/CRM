package com.example.demo.Repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.demo.Model.Field;
import com.example.demo.Model.Module;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ModuleRepository {

    private final CqlSession cqlSession;

    // PreparedStatement caching for high performance
    private final PreparedStatement psInsertModule;
    private final PreparedStatement psInsertOutbox;
    private final PreparedStatement psInsertField;
    private final PreparedStatement psDeleteModule;
    private final PreparedStatement psDeleteFields;
    private final PreparedStatement psDeleteRecords;
    private final PreparedStatement psSelectAllModules;
    private final PreparedStatement psSelectModuleById;
    private final PreparedStatement psSelectFieldsByApiName;
    private final PreparedStatement psSelectRecordsByApiName;

    public ModuleRepository(CqlSession cqlSession) {
        this.cqlSession = cqlSession;

        // 💡 Initialize Prepared Statements once at startup
        this.psInsertModule = cqlSession.prepare("INSERT INTO module_management.modules (api_name, module_name, created_date, record_id) VALUES (?, ?, ?, ?)");
        this.psInsertOutbox = cqlSession.prepare("INSERT INTO module_management.outbox (aggregate, event_id, event_type, payload) VALUES (?, ?, ?, ?)");
        this.psInsertField = cqlSession.prepare("INSERT INTO module_management.module_fields (api_name, field_api_name, field_display_name, field_data_type) VALUES (?, ?, ?, ?)");

        this.psDeleteModule = cqlSession.prepare("DELETE FROM module_management.modules WHERE api_name = ?");
        this.psDeleteFields = cqlSession.prepare("DELETE FROM module_management.module_fields WHERE api_name = ?");
        this.psDeleteRecords = cqlSession.prepare("DELETE FROM module_management.module_records WHERE api_name = ?");

        this.psSelectAllModules = cqlSession.prepare("SELECT * FROM module_management.modules");
        // Note: ALLOW FILTERING is used assuming record_id is not the primary key. If you index record_id, ALLOW FILTERING can be removed.
        this.psSelectModuleById = cqlSession.prepare("SELECT * FROM module_management.modules WHERE record_id = ? ALLOW FILTERING");
        this.psSelectFieldsByApiName = cqlSession.prepare("SELECT * FROM module_management.module_fields WHERE api_name = ?");
        this.psSelectRecordsByApiName = cqlSession.prepare("SELECT * FROM module_management.module_records WHERE api_name = ?");
    }

    // --- Helper Mappers ---

    private Module mapRowToModule(Row row) {
        long recordId = row.getLong("record_id");
        String moduleName = row.getString("module_name");
        String apiName = row.getString("api_name");

        // Convert Cassandra Instant back to java.sql.Date for your model
        Instant createdInstant = row.getInstant("created_date");
        Date createdDate = createdInstant != null ? new Date(createdInstant.toEpochMilli()) : null;

        return new Module(moduleName, recordId, apiName, createdDate);
    }

    private Field mapRowToField(Row row) {
        String fieldApiName = row.getString("field_api_name");
        String fieldDisplayName = row.getString("field_display_name");
        String fieldDataType = row.getString("field_data_type");
        // Assuming your schema auto-generates field_id or you pull it if required
        Long fieldId = row.getColumnDefinitions().contains("field_id") ? row.getLong("field_id") : 0L;

        return new Field(fieldId, fieldApiName, fieldDisplayName, fieldDataType);
    }

    // --- Repository Methods ---

    public Module createModule(Module modulePayload) {
        String apiname = modulePayload.getApiName();
        if (apiname == null || !apiname.matches("^[a-zA-Z0-9_]{1,20}$")) {
            System.out.println("Invalid API Name detected: " + apiname);
            return null;
        }

        UUID eventId = Uuids.timeBased();
        Instant now = Instant.now();
        // Generating a numeric ID for the model logic
        long numericRecordId = System.currentTimeMillis();

        try {
            // 1. Insert into Outbox
            StringBuilder builder = new StringBuilder(64);
            builder.append("{\"moduleName\":\"").append(modulePayload.getModuleName())
                    .append("\",\"apiName\":\"").append(apiname).append("\"}");

            BoundStatement boundOutbox = psInsertOutbox.bind("MODULE", eventId, "CREATE", builder.toString());
            cqlSession.execute(boundOutbox);

            // 2. Insert Module Metadata
            BoundStatement boundModule = psInsertModule.bind(apiname, modulePayload.getModuleName(), now, numericRecordId);
            cqlSession.execute(boundModule);
            modulePayload.setRecordID(numericRecordId);
            modulePayload.setCreatedDate(new Date(now.toEpochMilli()));

            // 3. Insert Default Fields (replacing dynamic SQL table creation)
            BoundStatement boundField1 = psInsertField.bind(apiname, "Name", "Name", "String");
            BoundStatement boundField2 = psInsertField.bind(apiname, "Number", "Number", "Number");
            cqlSession.execute(boundField1);
            cqlSession.execute(boundField2);

            System.out.println("Module creation and schema setup completed successfully.");
            return modulePayload;

        } catch (Exception e) {
            System.err.println("Error during module creation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Module deleteModule(Module moduleData) {
        String apiName = moduleData.getApiName();
        if (apiName == null || apiName.trim().isEmpty()) {
            System.out.println("Invalid API Name detected for deletion.");
            return null;
        }

        UUID eventId = Uuids.timeBased();

        try {
            // 1. Emitting the DELETE event to Outbox
            StringBuilder builder = new StringBuilder(64);
            builder.append("{\"moduleName\":\"").append(moduleData.getModuleName())
                    .append("\",\"apiName\":\"").append(apiName).append("\"}");

            BoundStatement boundOutbox = psInsertOutbox.bind("MODULE", eventId, "DELETE", builder.toString());
            cqlSession.execute(boundOutbox);

            // 2. Delete the Module Metadata (Partition key is api_name)
            BoundStatement boundDeleteModule = psDeleteModule.bind(apiName);
            cqlSession.execute(boundDeleteModule);

            // 3. Delete associated Fields and Records
            BoundStatement boundDeleteFields = psDeleteFields.bind(apiName);
            cqlSession.execute(boundDeleteFields);

            BoundStatement boundDeleteRecords = psDeleteRecords.bind(apiName);
            cqlSession.execute(boundDeleteRecords);

            System.out.println("Module and all associated schema data deleted successfully.");
            return moduleData;

        } catch (Exception e) {
            System.err.println("Error during module deletion: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Module> getModules() {
        List<Module> modules = new ArrayList<>();
        try {
            ResultSet rs = cqlSession.execute(psSelectAllModules.bind());
            for (Row row : rs) {
                modules.add(mapRowToModule(row));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching modules", e);
        }
        return modules;
    }

    public List<Map<String, Object>> getModulesByID(long moduleID) {
        List<Map<String, Object>> recordList = new ArrayList<>();
        try {
            // Get module definition
            ResultSet rsModule = cqlSession.execute(psSelectModuleById.bind(moduleID));
            Row moduleRow = rsModule.one();

            if (moduleRow != null) {
                Module module = mapRowToModule(moduleRow);

                // Example: We are keeping your requested return type List<Map>
                // which simulates dynamic tabular results.
                ResultSet rsRecords = cqlSession.execute(psSelectRecordsByApiName.bind(module.getApiName()));

                for (Row row : rsRecords) {
                    Map<String, Object> objList = new HashMap<>();
                    ColumnDefinitions colDefs = row.getColumnDefinitions();

                    for (int i = 0; i < colDefs.size(); i++) {
                        String colName = colDefs.get(i).getName().asCql(true);
                        objList.put(colName, row.getObject(i));
                    }
                    recordList.add(objList);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching module records by ID", e);
        }
        return recordList;
    }

    public Module getModulesByIDAlone(Long moduleID) {
        try {
            ResultSet rs = cqlSession.execute(psSelectModuleById.bind(moduleID));
            Row row = rs.one();
            if (row != null) {
                return mapRowToModule(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching single module by ID", e);
        }
        return null;
    }
}