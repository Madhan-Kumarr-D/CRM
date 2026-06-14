import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function NewRecord() {
    const { ModuleApiName, RecordID } = useParams();
    const navigate = useNavigate();
    
    const [fields, setFields] = useState([]);
    const [formData, setFormData] = useState({});
    const [loading, setLoading] = useState(true);

    const isEditMode = Boolean(RecordID);

    useEffect(() => {
        const fetchFieldsAndData = async () => {
            try {
                // 1. Fetch the fields for the UI
                const fieldResponse = await fetch(`http://localhost:8083/api/v1/field/${ModuleApiName}`);
                let fieldData = [];
                if (fieldResponse.ok) {
                    fieldData = await fieldResponse.json();
                }

                // 2. Fetch record data if editing
                let initialFormData = {};
                if (isEditMode) {
                    const recordResponse = await fetch(`http://localhost:8083/api/v1/records/${ModuleApiName}/${RecordID}`);
                    if (recordResponse.ok) {
                        // Because your API already returns {"Name": "SO - 23", "Number": "13"}, 
                        // we can just directly assign it!
                        initialFormData = await recordResponse.json();
                    }
                }

                // 3. Set BOTH states at the exact same time
                setFields(fieldData);
                setFormData(initialFormData);
                setLoading(false);

            } catch (error) {
                console.error("Error fetching data:", error);
                setLoading(false);
            }
        };

        if (ModuleApiName) {
            fetchFieldsAndData();
        }
    }, [ModuleApiName, RecordID, isEditMode]);

    const handleInputChange = (e, fieldApiName) => {
        setFormData(prev => ({
            ...prev,
            [fieldApiName]: e.target.value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            const method = isEditMode ? 'PUT' : 'POST';
            const url = isEditMode 
                ? `http://localhost:8083/api/v1/records/${ModuleApiName}/${RecordID}` 
                : `http://localhost:8083/api/v1/records/${ModuleApiName}`;

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                alert(`Record ${isEditMode ? 'updated' : 'created'} successfully!`);
                navigate(`/${ModuleApiName}`); 
            } else {
                alert(`Failed to ${isEditMode ? 'update' : 'create'} record.`);
            }
        } catch (error) {
            console.error(`Error ${isEditMode ? 'updating' : 'creating'} record:`, error);
        }
    };

    const getInputType = (dataType) => {
        const type = dataType?.toLowerCase() || 'text';
        if (type.includes('int') || type.includes('number') || type.includes('decimal')) return 'number';
        if (type.includes('date')) return 'date';
        if (type.includes('boolean')) return 'checkbox';
        return 'text';
    };

    // --- INLINE STYLES ---
    const containerStyle = { padding: '40px', minHeight: '100vh', backgroundColor: '#001a00', color: '#ffffff', fontFamily: "sans-serif", display: 'flex', flexDirection: 'column', alignItems: 'center' };
    const formCardStyle = { backgroundColor: 'rgba(255, 255, 255, 0.05)', padding: '30px', borderRadius: '8px', width: '100%', maxWidth: '600px', boxShadow: '0 4px 6px rgba(0,0,0,0.3)', borderTop: '4px solid #04AA6D' };
    const titleStyle = { marginTop: 0, marginBottom: '20px', fontSize: '1.8rem', color: '#04AA6D', textAlign: 'center', textTransform: 'capitalize' };
    const formGroupStyle = { marginBottom: '20px', display: 'flex', flexDirection: 'column' };
    const labelStyle = { marginBottom: '8px', fontWeight: 'bold', color: '#e0e0e0', fontSize: '0.95rem' };
    const inputStyle = { padding: '12px', borderRadius: '4px', border: '1px solid #04AA6D', backgroundColor: '#002b00', color: '#ffffff', fontSize: '1rem', outline: 'none', transition: 'border-color 0.3s' };
    const buttonContainerStyle = { display: 'flex', justifyContent: 'space-between', marginTop: '30px' };
    const submitBtnStyle = { backgroundColor: '#04AA6D', color: 'white', padding: '12px 24px', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold', fontSize: '1rem', flex: '1', marginLeft: '10px', transition: '0.3s' };
    const cancelBtnStyle = { backgroundColor: 'transparent', color: '#04AA6D', padding: '12px 24px', border: '2px solid #04AA6D', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold', fontSize: '1rem', flex: '1', marginRight: '10px', transition: '0.3s' };

    if (loading) return <div style={{ ...containerStyle, justifyContent: 'center' }}><h2 style={{ color: '#04AA6D' }}>Loading...</h2></div>;

    return (
        <div style={containerStyle}>
            <div style={formCardStyle}>
                <h2 style={titleStyle}>{isEditMode ? 'Edit' : 'Create New'} {ModuleApiName}</h2>
                
                <form onSubmit={handleSubmit}>
                    {fields.length > 0 ? (
                        fields.map((field) => {
                            // Fallbacks for capitalization differences from Spring Boot Jackson
                            const apiName = field.fieldApiName || field.FieldApiName;
                            const displayName = field.fieldDisplayName || field.FieldDisplayName;
                            const dataType = field.fieldDataType || field.FieldDataType;
                            const fieldId = field.fieldID || field.FieldID;

                            return (
                                <div key={fieldId} style={formGroupStyle}>
                                    <label htmlFor={apiName} style={labelStyle}>
                                        {displayName}
                                    </label>
                                    <input
                                        type={getInputType(dataType)}
                                        id={apiName}
                                        name={apiName}
                                        style={inputStyle}
                                        // This will correctly bind the state to the input
                                        value={formData[apiName] || ''} 
                                        onChange={(e) => handleInputChange(e, apiName)}
                                        required
                                        onFocus={(e) => e.target.style.boxShadow = '0 0 8px #04AA6D'}
                                        onBlur={(e) => e.target.style.boxShadow = 'none'}
                                    />
                                </div>
                            );
                        })
                    ) : (
                        <p style={{ textAlign: 'center', color: '#888' }}>No fields configured for this module.</p>
                    )}

                    <div style={buttonContainerStyle}>
                        <button 
                            type="button" 
                            onClick={() => navigate(`/${ModuleApiName}`)} 
                            style={cancelBtnStyle}
                            onMouseOver={(e) => e.target.style.backgroundColor = 'rgba(4, 170, 109, 0.1)'}
                            onMouseOut={(e) => e.target.style.backgroundColor = 'transparent'}
                        >
                            Cancel
                        </button>
                        <button 
                            type="submit" 
                            style={submitBtnStyle}
                            onMouseOver={(e) => e.target.style.backgroundColor = '#05935d'}
                            onMouseOut={(e) => e.target.style.backgroundColor = '#04AA6D'}
                        >
                            {isEditMode ? 'Update Record' : 'Save Record'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default NewRecord;