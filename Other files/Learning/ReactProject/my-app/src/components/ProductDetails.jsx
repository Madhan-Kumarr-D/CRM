// ProductDetails.jsx
import React, { useEffect, useState } from "react";
import { useParams, Link, Navigate } from "react-router-dom";
import { useNavigate } from "react-router-dom";

export default function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

   function handleClickUpdate(ProductID){
    navigate("/update-product/"+ProductID);
   };


  useEffect(() => {
    fetch(`/api/products/${id}`)
      .then((res) => res.json())
      .then((data) => {
        setProduct(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error fetching product:", err);
        setLoading(false);
      });
  }, [id]);

  if (loading)
    return (
      <div style={pageStyle}>
        <div style={cardStyle}>
          <p style={{ color: "#a5b4fc", fontSize: 20 }}>⏳ Loading product...</p>
        </div>
      </div>
    );

  if (!product)
    return (
      <div style={pageStyle}>
        <div style={cardStyle}>
          <p style={{ color: "#f87171", fontSize: 20 }}>⚠ Product not found</p>
        </div>
      </div>
    );

  return (
    <div style={pageStyle}>
      <div style={cardStyle} className="fade-in">
        <h1 style={titleStyle}>{product.productName}</h1>

        <div style={infoBox}>
          <p>
            <span style={labelStyle}>🆔 Serial Number:</span>{" "}
            <span style={valueStyle}>{product.productID}</span>
          </p>
          <p>
            <span style={labelStyle}>📦 Stock:</span>{" "}
            <span style={valueStyle}>{product.productCount}</span>
          </p>
        </div>
        <button style={buttonStyle} className="btn-glow">
            Buy Now !
        </button>
                  <br/>
        <br/>
        {/* <button style={buttonStyle} className="btn-glow" onClick={() => handleClickUpdate(product.productID)}>
            Update Product 
        </button>
                  <br/>
        <br/> */}
        <Link to="/" style={buttonStyle} className="btn-glow">
          ⬅ Back to Products
        </Link>
      </div>
    </div>
  );
}

// 🎨 Styles
const pageStyle = {
  minHeight: "100vh",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  background: "linear-gradient(135deg, #0f172a, #1e293b, #111827)",
  padding: 24,
};

const cardStyle = {
  background: "rgba(255,255,255,0.05)",
  backdropFilter: "blur(12px)",
  borderRadius: 20,
  padding: "40px 50px",
  textAlign: "center",
  boxShadow: "0 8px 30px rgba(0,0,0,0.5)",
  maxWidth: 500,
  width: "100%",
  animation: "fadeIn 0.8s ease-in-out",
};

const titleStyle = {
  fontSize: 36,
  fontWeight: 800,
  background: "linear-gradient(135deg, #6366f1, #a78bfa)",
  WebkitBackgroundClip: "text",
  color: "transparent",
  marginBottom: 20,
};

const infoBox = {
  background: "rgba(99,102,241,0.1)",
  borderRadius: 12,
  padding: "20px 15px",
  marginBottom: 30,
  color: "#e5e7eb",
  fontSize: 18,
};

const labelStyle = { fontWeight: 600, color: "#a5b4fc" };
const valueStyle = { fontWeight: 500, color: "#f9fafb" };

const buttonStyle = {
  padding: "12px 24px",
  borderRadius: 12,
  background: "linear-gradient(135deg, #6366f1, #4f46e5)",
  color: "white",
  textDecoration: "none",
  fontSize: 16,
  fontWeight: 600,
  transition: "all 0.3s ease",
  display: "inline-block",
};

// ✅ Extra animations (add in your global CSS)
