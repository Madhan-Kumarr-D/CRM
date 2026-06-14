import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function ModulePage() {
    const { ModuleApiName } = useParams();
    const navigate = useNavigate();
    
    const [fields, setFields] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // State for the new field form at the bottom
    const [newField, setNewField] = useState({
        fieldDisplayName: '',
        fieldApiName: '',
        fieldDataType: 'String' // Default selection
    });

    // Fetch existing fields on load
    useEffect(() => {
        const fetchFields = async () => {
            try {
                const response = await fetch(`http://localhost:8083/api/v1/field/${ModuleApiName}`);
                if (response.ok) {
                    const data = await response.json();
                    setFields(data);
                }
                setLoading(false);
            } catch (error) {
                console.error("Error fetching fields:", error);
                setLoading(false);
            }
        };

        if (ModuleApiName) {
            fetchFields();
        }
    }, [ModuleApiName]);

    // Handle inputs for the new field
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        
        // If typing the Display Name, automatically generate a safe API Name (no spaces)
        if (name === 'fieldDisplayName' && !newField.fieldApiNameModified) {
            const autoApiName = value.replace(/[^a-zA-Z0-9_]/g, '');
            setNewField({
                ...newField,
                fieldDisplayName: value,
                fieldApiName: autoApiName
            });
        } else {
            setNewField({
                ...newField,
                [name]: value,
                ...(name === 'fieldApiName' ? { fieldApiNameModified: true } : {})
            });
        }
    };

    // Add Field POST call
    const handleAddField = async (e) => {
        e.preventDefault();
        
        // Backend validation check (matches your Java regex ^[a-zA-Z0-9_]{1,20}$)
       if (!/^[a-zA-Z0-9_]{1,20}$/.test(newField.fieldApiName)) {
            alert("API Name can only contain letters, numbers, and underscores (max 20 chars).");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8083/api/v1/field/${ModuleApiName}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    fieldDisplayName: newField.fieldDisplayName,
                    fieldApiName: newField.fieldApiName,
                    fieldDataType: newField.fieldDataType
                })
            });

            if (response.ok) {
                const addedField = await response.json();
                // Add the new field to the table immediately
                setFields([...fields, addedField]);
                // Clear the form
                setNewField({ fieldDisplayName: '', fieldApiName: '', fieldDataType: 'String' });
                alert("Field added successfully!");
            } else {
                alert("Failed to add field. Make sure the API name doesn't already exist.");
            }
        } catch (error) {
            console.error("Error adding field:", error);
            alert("An error occurred while adding the field.");
        }
    };

    // --- Inline Styles ---
    const containerStyle = { padding: '40px', minHeight: '100vh', backgroundColor: '#001a00', color: '#ffffff', fontFamily: "sans-serif", display: 'flex', flexDirection: 'column', alignItems: 'center' };
    const contentWidth = { width: '100%', maxWidth: '800px' };
    const tableStyle = { ...contentWidth, borderCollapse: 'collapse', backgroundColor: 'rgba(255, 255, 255, 0.05)', borderRadius: '8px', overflow: 'hidden', boxShadow: '0 4px 6px rgba(0,0,0,0.3)', marginBottom: '40px' };
    const thTdStyle = { padding: '15px', textAlign: 'left', borderBottom: '1px solid #333' };
    const headerStyle = { ...thTdStyle, backgroundColor: '#04AA6D', color: 'white' };
    
    // Form Styles
    const formCardStyle = { ...contentWidth, backgroundColor: 'rgba(255, 255, 255, 0.05)', padding: '25px', borderRadius: '8px', borderTop: '4px solid #04AA6D' };
    const inputStyle = { padding: '10px', borderRadius: '4px', border: '1px solid #04AA6D', backgroundColor: '#002b00', color: '#ffffff', fontSize: '1rem', width: '100%', boxSizing: 'border-box', marginTop: '5px' };
    const submitBtnStyle = { backgroundColor: '#04AA6D', color: 'white', padding: '12px 24px', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold', fontSize: '1rem', marginTop: '20px', width: '100%' };

    if (loading) return <div style={{...containerStyle, justifyContent:'center'}}><h2 style={{color: '#04AA6D'}}>Loading fields...</h2></div>;

    return (
        <div style={containerStyle}>
            {/* Header */}
            <div style={{ ...contentWidth, display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <button onClick={() => navigate(`/${ModuleApiName}`)} style={{ backgroundColor: 'transparent', color: '#04AA6D', border: '1px solid #04AA6D', padding: '8px 15px', borderRadius: '5px', cursor: 'pointer' }}>
                    &larr; Back to Records
                </button>
                <h2 style={{ color: '#04AA6D', margin: 0 }}>Manage Fields: {ModuleApiName}</h2>
                <div style={{ width: '100px' }}></div> {/* Spacer for centering */}
            </div>

            {/* Existing Fields Table */}
            <table style={tableStyle}>
                <thead>
                    <tr>
                        <th style={headerStyle}>Display Name</th>
                        <th style={headerStyle}>API Name (DB Column)</th>
                        <th style={headerStyle}>Data Type</th>
                    </tr>
                </thead>
                <tbody>
                    {fields.length > 0 ? (
                        fields.map((field, index) => (
                            <tr key={field.fieldID || index}>
                                <td style={thTdStyle}>{field.fieldDisplayName || field.FieldDisplayName}</td>
                                <td style={thTdStyle}>{field.fieldApiName || field.FieldApiName}</td>
                                <td style={thTdStyle}>{field.fieldDataType || field.FieldDataType}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="3" style={{...thTdStyle, textAlign: 'center', color: '#888'}}>No fields created yet.</td>
                        </tr>
                    )}
                </tbody>
            </table>

            {/* Add New Field Form */}
            <div style={formCardStyle}>
                <h3 style={{ marginTop: 0, color: '#04AA6D' }}>+ Add New Field</h3>
                <form onSubmit={handleAddField} style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
                    
                    <div style={{ flex: '1', minWidth: '200px' }}>
                        <label style={{ fontSize: '0.9rem', color: '#e0e0e0' }}>Field Display Name</label>
                        <input
                            type="text"
                            name="fieldDisplayName"
                            value={newField.fieldDisplayName}
                            onChange={handleInputChange}
                            placeholder="e.g. Email Address"
                            style={inputStyle}
                            required
                        />
                    </div>

                    <div style={{ flex: '1', minWidth: '200px' }}>
                        <label style={{ fontSize: '0.9rem', color: '#e0e0e0' }}>Field API Name</label>
                        <input
                            type="text"
                            name="fieldApiName"
                            value={newField.fieldApiName}
                            onChange={handleInputChange}
                            placeholder="e.g. Email_Address"
                            style={inputStyle}
                            required
                        />
                        <small style={{ color: '#888', fontSize: '0.75rem' }}>No spaces allowed.</small>
                    </div>

                    <div style={{ flex: '1', minWidth: '150px' }}>
                        <label style={{ fontSize: '0.9rem', color: '#e0e0e0' }}>Data Type</label>
                        <select
                            name="fieldDataType"
                            value={newField.fieldDataType}
                            onChange={handleInputChange}
                            style={inputStyle}
                        >
                            <option value="String">Text / String</option>
                            <option value="Number">Number</option>
                            <option value="Date">Date</option>
                            <option value="Boolean">Boolean (Checkbox)</option>
                        </select>
                    </div>

                    <div style={{ width: '100%' }}>
                        <button type="submit" style={submitBtnStyle}>
                            Create Field
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ModulePage;