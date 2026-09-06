package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Module {
    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleName(String moduleName) {
        this.ModuleName = moduleName;
    }

    public long getRecordID() {
        return RecordID;
    }

    public void setRecordID(long recordID) {
        this.RecordID = recordID;
    }

    public String getApiName() {
        return ApiName;
    }

    public void setApiName(String apiName) {
        this.ApiName = apiName;
    }

    public Date getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.CreatedDate = createdDate;
    }

    @Override
    public String toString() {
        return "Module{" +
                "ModuleName='" + ModuleName + '\'' +
                ", RecordID=" + RecordID +
                ", ApiName='" + ApiName + '\'' +
                ", CreatedDate=" + CreatedDate +
                '}';
    }

    @JsonProperty("ModuleName")
    private String ModuleName;
    public Module(){

    }

    public Module(String moduleName, long recordID, String apiName, Date createdDate) {
        this.ModuleName = moduleName;
        this.RecordID = recordID;
        this.ApiName = apiName;
        this.CreatedDate = createdDate;
    }

    private long RecordID;
    @JsonProperty("ApiName")
    private String ApiName;
    private Date CreatedDate;

}
