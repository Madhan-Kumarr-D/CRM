import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";

export default function AddProduct() {
  const navigate = useNavigate();
  const { productid } = useParams(); // <-- gets ID from /update-product/:productid

  const [formData, setFormData] = useState({
    productName: "",
    productCount: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // 🔹 Fetch product if we are in "update" mode
  useEffect(() => {
    if (productid) {
      setLoading(true);
      fetch(`/api/products/${productid}`)
        .then((res) => {
          if (!res.ok) throw new Error("Failed to fetch product");
          return res.json();
        })
        .then((data) => {
          setFormData({
            productName: data.productName,
            productCount: data.productCount,
          });
        })
        .catch((err) => setError("❌ Failed to load product."))
        .finally(() => setLoading(false));
    }
  }, [productid]);

  // handle input change
  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  // handle submit
  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const res = await fetch("/api/products", {
        method: productid ? "PUT" : "POST", // add vs update
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          productID: productid ? parseInt(productid, 10) : undefined, // needed for update
          productName: formData.productName,
          productCount: parseInt(formData.productCount, 10),
        }),
      });

      if (!res.ok) throw new Error("Request failed");

      alert(
        productid
          ? "✅ Product updated successfully!"
          : "✅ Product added successfully!"
      );
      navigate("/"); // back to product list
    } catch (err) {
      console.error(err);
      setError("❌ Failed to save product. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div
      style={{
        height: "100vh",
        width: "100vw",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: "linear-gradient(135deg, #0f172a, #1e293b)",
        color: "white",
        fontFamily: "Inter, sans-serif",
      }}
    >
      <form
        onSubmit={handleSubmit}
        style={{
          background: "rgba(255,255,255,0.08)",
          padding: "36px 44px",
          borderRadius: 20,
          boxShadow: "0 12px 28px rgba(0,0,0,0.6)",
          width: 380,
        }}
      >
        <h2
          style={{
            textAlign: "center",
            marginBottom: 28,
            fontSize: 24,
            fontWeight: 700,
          }}
        >
          {productid ? "Update Product" : "Add New Product"}
        </h2>

        <label style={{ display: "block", marginBottom: 14 }}>
          Product Name
          <input
            type="text"
            name="productName"
            value={formData.productName}
            onChange={handleChange}
            required
            style={{
              width: "100%",
              padding: "12px",
              borderRadius: 10,
              border: "1px solid #374151",
              marginTop: 6,
              marginBottom: 18,
              background: "#111827",
              color: "white",
              fontSize: 15,
            }}
          />
        </label>

        <label style={{ display: "block", marginBottom: 14 }}>
          Product Count
          <input
            type="number"
            name="productCount"
            value={formData.productCount}
            onChange={handleChange}
            required
            style={{
              width: "100%",
              padding: "12px",
              borderRadius: 10,
              border: "1px solid #374151",
              marginTop: 6,
              marginBottom: 18,
              background: "#111827",
              color: "white",
              fontSize: 15,
            }}
          />
        </label>

        {error && (
          <p style={{ color: "red", marginBottom: 16, fontSize: 14 }}>{error}</p>
        )}

        <button
          type="submit"
          disabled={loading}
          style={{
            width: "100%",
            padding: "14px",
            borderRadius: 12,
            background: "linear-gradient(135deg, #6366f1, #4f46e5)",
            color: "white",
            fontWeight: 600,
            fontSize: 16,
            border: "none",
            cursor: "pointer",
          }}
        >
          {loading
            ? productid
              ? "Updating..."
              : "Adding..."
            : productid
            ? "Update Product"
            : "Add Product"}
        </button>
      </form>
    </div>
  );
}
