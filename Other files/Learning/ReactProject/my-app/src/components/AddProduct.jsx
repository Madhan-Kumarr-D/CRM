import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function AddProduct() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    productName: "",
    productCount: "",
    productImage: null,
  });

  const [preview, setPreview] = useState(null);

  const handleChange = (e) => {
    const { name, value, files } = e.target;

    if (name === "productImage") {
      const file = files[0];
      setFormData({ ...formData, productImage: file });
      setPreview(URL.createObjectURL(file));
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const data = new FormData();
    data.append("productName", formData.productName);
    data.append("productCount", formData.productCount);
    if (formData.productImage) {
      data.append("productImage", formData.productImage);
    }

    fetch("/api/products", {
  method: "POST",
  body: data,
})
  .then((res) => {
    if (!res.ok) throw new Error("Failed to save");
    return res.text(); // ✅ works even if backend returns nothing
  })
  .then(() => {
    console.log("navigating...");
    navigate("/");
  })
  .catch((err) => {
    console.error("Error while saving:", err);
  });

  };

  return (
    <div className="form-container fade-in scale-in">
      {/* ✅ Embedded Global + Motion Styles */}
      <style>{`
        :root {
          font-family: system-ui, Avenir, Helvetica, Arial, sans-serif;
          line-height: 1.5;
          font-weight: 400;
          color-scheme: light dark;
          color: rgba(255, 255, 255, 0.87);
          background-color: #242424;
          -webkit-font-smoothing: antialiased;
          -moz-osx-font-smoothing: grayscale;
        }

        /* Fade + Scale */
        .fade-in { animation: fadeIn 0.8s ease-in-out; }
        .scale-in { animation: scaleIn 0.6s ease-in-out; }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(20px); }
          to { opacity: 1; transform: translateY(0); }
        }
        @keyframes scaleIn {
          from { transform: scale(0.9); opacity: 0; }
          to { transform: scale(1); opacity: 1; }
        }

        /* Container */
        .form-container {
          max-width: 450px;
          margin: auto;
          padding: 2rem;
          border-radius: 16px;
          background: rgba(30, 30, 30, 0.7);
          backdrop-filter: blur(8px);
          box-shadow: 0 6px 20px rgba(0,0,0,0.4);
          transition: transform 0.3s ease;
        }
        .form-container:hover {
          transform: translateY(-4px) scale(1.01);
        }

        .form-content {
          display: flex;
          flex-direction: column;
          gap: 1rem;
        }

        /* Inputs */
        .input-field {
          padding: 0.8rem;
          border: 1px solid #646cff55;
          border-radius: 10px;
          font-size: 1rem;
          background-color: #1a1a1a;
          color: inherit;
          transition: all 0.3s ease;
        }
        .input-field:hover {
          border-color: #8b8dff;
          box-shadow: 0 0 10px rgba(99, 102, 241, 0.4);
          transform: scale(1.02);
        }
        .input-field:focus {
          border-color: #646cff;
          background-color: #2a2a2a;
          outline: none;
          transform: scale(1.03);
        }

        /* Buttons */
        .btn-glow {
          padding: 0.8rem 1.4rem;
          border-radius: 10px;
          border: none;
          font-size: 1rem;
          font-weight: 600;
          cursor: pointer;
          color: white;
          background: linear-gradient(135deg, #646cff, #535bf2);
          transition: all 0.4s ease;
          position: relative;
          overflow: hidden;
        }
        .btn-glow::after {
          content: "";
          position: absolute;
          top: 0; left: -75%;
          width: 50%; height: 100%;
          background: rgba(255,255,255,0.3);
          transform: skewX(-20deg);
          transition: left 0.5s ease;
        }
        .btn-glow:hover::after {
          left: 130%;
        }
        .btn-glow:hover {
          box-shadow: 0 0 20px rgba(99,102,241,0.8);
          transform: translateY(-2px) scale(1.05);
        }

        /* Image Preview */
        .preview-box {
          display: flex;
          justify-content: center;
          align-items: center;
          margin-top: 1rem;
        }
        .preview-img {
          width: 140px;
          height: 140px;
          object-fit: cover;
          border-radius: 12px;
          border: 2px solid #646cff;
          transition: transform 0.4s ease, box-shadow 0.4s ease;
        }
        .preview-img:hover {
          transform: scale(1.1) rotate(2deg);
          box-shadow: 0 8px 20px rgba(99,102,241,0.6);
        }

        h2 {
          text-align: center;
          margin-bottom: 1.2rem;
        }

        @media (prefers-color-scheme: light) {
          :root {
            color: #213547;
            background-color: #ffffff;
          }
          .input-field { background-color: #f9f9f9; }
          .form-container { background: rgba(255,255,255,0.8); }
        }
      `}</style>

      <h2>Add Product</h2>

      <form onSubmit={handleSubmit} className="form-content">
        <input
          type="text"
          name="productName"
          placeholder="Product Name"
          value={formData.productName}
          onChange={handleChange}
          className="input-field"
        />

        <input
          type="number"
          name="productCount"
          placeholder="Product Count"
          value={formData.productCount}
          onChange={handleChange}
          className="input-field"
        />

        <input
          type="file"
          name="productImage"
          accept="image/*"
          onChange={handleChange}
          className="input-field"
        />

        {preview && (
          <div className="preview-box fade-in">
            <img src={preview} alt="Preview" className="preview-img" />
          </div>
        )}

        <button type="submit" className="btn-glow">
          Save
        </button>
      </form>
    </div>
  );
}
