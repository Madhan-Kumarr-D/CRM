import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function ListRecord() {
    const { ModuleApiName } = useParams();
    const navigate = useNavigate();
    const [records, setRecords] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchRecords = async () => {
            try {
                // Using your GET endpoint for records
                const response = await fetch(`http://localhost:8083/api/v1/records/${ModuleApiName}`);
                const data = await response.json();
                setRecords(data);
                setLoading(false);
            } catch (error) {
                console.error("Error fetching records:", error);
                setLoading(false);
            }
        };

        if (ModuleApiName) {
            fetchRecords();
        }
    }, [ModuleApiName]);

    // --- Inline Styles ---
    const containerStyle = {
        padding: '40px', minHeight: '100vh', backgroundColor: '#001a00',
        color: '#ffffff', fontFamily: "sans-serif", display: 'flex',
        flexDirection: 'column', alignItems: 'center'
    };
    
    const tableStyle = {
        width: '100%', maxWidth: '900px', borderCollapse: 'collapse',
        backgroundColor: 'rgba(255, 255, 255, 0.05)', borderRadius: '8px',
        overflow: 'hidden', boxShadow: '0 4px 6px rgba(0,0,0,0.3)'
    };

    const thTdStyle = {
        padding: '15px', textAlign: 'left', borderBottom: '1px solid #333'
    };

    const headerStyle = { ...thTdStyle, backgroundColor: '#04AA6D', color: 'white' };
    
    const rowStyle = { cursor: 'pointer', transition: 'background-color 0.2s' };

    // Button Styles
    const backBtnStyle = {
        backgroundColor: 'transparent', color: '#04AA6D', padding: '10px 15px', 
        border: '1px solid #04AA6D', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold'
    };

    const manageFieldsBtnStyle = {
        backgroundColor: '#2196F3', color: 'white', padding: '10px 15px', 
        border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold', marginRight: '10px'
    };

    const createRecordBtnStyle = {
        backgroundColor: '#04AA6D', color: 'white', padding: '10px 15px', 
        border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold'
    };

    if (loading) return <div style={{...containerStyle, justifyContent:'center'}}><h2 style={{color: '#04AA6D'}}>Loading records...</h2></div>;

    return (
        <div style={containerStyle}>
            {/* Header Area with Buttons */}
            <div style={{ width: '100%', maxWidth: '900px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                
                {/* Left Side: Back Button */}
                <div>
                    <button 
                        onClick={() => navigate('/')} 
                        style={backBtnStyle}
                        onMouseOver={(e) => e.target.style.backgroundColor = 'rgba(4, 170, 109, 0.1)'}
                        onMouseOut={(e) => e.target.style.backgroundColor = 'transparent'}
                    >
                        &larr; Back
                    </button>
                </div>

                {/* Center: Title */}
                <h2 style={{ color: '#04AA6D', margin: 0 }}>{ModuleApiName} Records</h2>

                {/* Right Side: Action Buttons */}
                <div>
                    <button 
                        // Navigates to the Module Page where you can edit/create fields
                        onClick={() => navigate(`/modulepage/${ModuleApiName}`)} 
                        style={manageFieldsBtnStyle}
                        onMouseOver={(e) => e.target.style.backgroundColor = '#1976D2'}
                        onMouseOut={(e) => e.target.style.backgroundColor = '#2196F3'}
                    >
                        ⚙ Manage Fields
                    </button>
                    
                    <button 
                        onClick={() => navigate(`/create/${ModuleApiName}`)}
                        style={createRecordBtnStyle}
                        onMouseOver={(e) => e.target.style.backgroundColor = '#05935d'}
                        onMouseOut={(e) => e.target.style.backgroundColor = '#04AA6D'}
                    >
                        + New Record
                    </button>
                </div>
            </div>

            {/* Table Area */}
            <table style={tableStyle}>
                <thead>
                    <tr>
                        <th style={headerStyle}>Name</th>
                        <th style={headerStyle}>Number</th>
                    </tr>
                </thead>
                <tbody>
                    {records.length > 0 ? (
                        records.map((record, index) => (
                            <tr 
                                key={record.RecordID || index} 
                                style={rowStyle}
                                onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.1)'}
                                onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                                // Navigate to the edit page when clicked, passing the RecordID
                                onClick={() => navigate(`/edit/${ModuleApiName}/${record.RecordID}`)}
                            >
                                <td style={thTdStyle}>{record.Name}</td>
                                <td style={thTdStyle}>{record.Number}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="2" style={{...thTdStyle, textAlign: 'center', color: '#888'}}>No records found.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
}

export default ListRecord;