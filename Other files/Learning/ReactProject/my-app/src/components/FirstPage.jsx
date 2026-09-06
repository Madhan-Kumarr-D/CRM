// React - Beautiful Product Cards (App.jsx)
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const styles = {
  page: {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
    background: "linear-gradient(135deg, #0f172a, #1e293b)",
    fontFamily:
      "Inter, ui-sans-serif, system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial",
    position: "relative", // ✅ Needed for button positioning
  },
  search:{
    position: "absolute",
    top: 50,
    right: 30,
    padding: "12px 18px",
    borderRadius: 120,
    background: "linear-gradient(135deg, #fc0af0ff, #f770ebff)",
    color: "white",
    fontSize: 20,
    fontWeight: 700,
    cursor: "pointer",
    boxShadow: "0 6px 16px rgba(170, 34, 197, 0.5)",
    transition: "all 0.3s ease",
    border: "2px solid black"
  },
  addButton: {
    position: "absolute",
    top: 40,
    left: 30,
    padding: "12px 18px",
    borderRadius: 50,
    background: "linear-gradient(135deg, #22c55e, #16a34a)",
    color: "white",
    fontSize: 20,
    fontWeight: 700,
    border: "none",
    cursor: "pointer",
    boxShadow: "0 6px 16px rgba(34,197,94,0.5)",
    transition: "all 0.3s ease",
  },
  addButtonHover: {
    background: "linear-gradient(135deg, #15803d, #166534)",
    transform: "scale(1.08)",
    boxShadow: "0 8px 20px rgba(34,197,94,0.7)",
  },
  container: {
    maxWidth: 1100,
    width: "100%",
  },
  header: {
    marginBottom: 40,
    textAlign: "center",
  },
  heading: {
    fontSize: 36,
    fontWeight: 800,
    color: "#f9fafb",
    margin: 0,
    letterSpacing: "-0.5px",
  },
  sub: {
    color: "#9ca3af",
    marginTop: 10,
    fontSize: 18,
  },
  grid: {
    display: "flex",
    justifyContent: "center",
    gap: 24,
    flexWrap: "wrap",
  },
  card: {
    background: "linear-gradient(145deg, #1f2937, #111827)",
    borderRadius: 20,
    boxShadow: "0 8px 20px rgba(0,0,0,0.6)",
    padding: 24,
    width: 280,
    textAlign: "center",
    transition: "transform 0.3s ease, box-shadow 0.3s ease",
    border: "1px solid rgba(255,255,255,0.08)",
  },
  cardHover: {
    transform: "translateY(-10px) scale(1.03)",
    boxShadow: "0 14px 32px rgba(0,0,0,0.8), 0 0 12px rgba(79,70,229,0.5)",
  },
  title: {
    fontSize: 20,
    fontWeight: 700,
    margin: "0 0 12px 0",
    color: "#e5e7eb",
  },
  body: { margin: "0 0 16px 0", color: "#cbd5e1", fontSize: 16 },
  button: {
    display: "inline-block",
    padding: "12px 18px",
    borderRadius: 14,
    background: "linear-gradient(135deg, #6366f1, #4f46e5)",
    color: "#ffffff",
    cursor: "pointer",
    border: "none",
    fontSize: 15,
    fontWeight: 600,
    transition: "all 0.3s ease",
  },
  buttonHover: {
    background: "linear-gradient(135deg, #4338ca, #3730a3)",
    transform: "scale(1.05)",
    boxShadow: "0 4px 12px rgba(79,70,229,0.5)",
  },
};

export default function FirstPage() {
  
  const [items, setItems] = useState([]);
  const [products, setProducts] = useState([]);
  const [hovered, setHovered] = useState(null);
  const [btnHover, setBtnHover] = useState(null);
  const [addBtnHover, setAddBtnHover] = useState(false);

   const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  
  
  function HandleSearch(event){
    console.log("inside search");
    if(event.target.value != "")
    {fetch("/api/products/search?value="+event.target.value)
    .then((res)=>res.json())
    .then((data)=>{
      console.log(data);
      let temp=[];
    for (let i = 0; i < data.length; i++) {
        temp.push(data[i]["productName"])// Access element by index
    }
setItems(temp);
    }).catch((err) => console.error("Error fetching products:", err));
  }}

  useEffect(() => {
    fetch("/api/products")
      .then((res) => res.json())
      .then((data) => setProducts(data))
      .catch((err) => console.error("Error fetching products:", err));
  }, []);

  function handleClick(productID) {
    navigate(`/products/${productID}`);
  }

  async function handleClickDelete(productID) {
    setLoading(true);
    try{
      const res = await fetch("/api/products/"+productID,{
        method:"DELETE",
      });

      if (!res.ok) {
      const msg = await res.text().catch(() => "");
      throw new Error(msg || `Failed with ${res.status}`);
    }
    // alert("product deleted successfully")
    setProducts((prev) => prev.filter((p) => p.productID !== productID));
    }
    catch (err) {
    if (err.name !== "AbortError") {
      console.error(err);
      setError("❌ Failed to delete product. Please try again.");
    }
  } finally {
    setLoading(false);
  }
  }

  function handleAddProduct() {
    navigate("/add-product"); // ✅ You can create a form page for adding products
  }

  return (
    <main style={styles.page}>
      {/* Floating Add Product Button */}
      <button
        style={{
          ...styles.addButton,
          ...(addBtnHover ? styles.addButtonHover : {}),
        }}
        onMouseEnter={() => setAddBtnHover(true)}
        onMouseLeave={() => setAddBtnHover(false)}
        onClick={handleAddProduct}
      >
        ＋ Add
      </button>

      <div style={styles.container}>
        <header style={styles.header}>
          <h1 style={styles.heading}>Grocery Products</h1>
          <p style={styles.sub}>Time to buy</p>
          <input type="search" style={styles.search} onChange={HandleSearch}/>
          <ul
        style={{
          position: "absolute",
          top: 100, // slightly below the search bar
          right: 30,
          background: "blue",
          color: "red",
          paddingleft: "50px",
          paddingRight:"180px",
          borderRadius: "10px",
          listStyle: "none",
          boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
        }}
      >
        {items.map((item, index) => (
          <li key={index} style={{ padding: "6px 10px" }}>
            {item}
          </li>
        ))}
      </ul>
        </header>

        <section style={styles.grid} aria-label="cards">
          {products.length === 0 ? (
            <p style={{ color: "white", fontSize: 18 }}>Loading products...</p>
          ) : (
            products.map((product) => {
              const isHovered = hovered === product.productID;
              const isBtnHovered = btnHover === product.productID;

              return (
                <article
                  key={product.productID}
                  style={{
                    ...styles.card,
                    ...(isHovered ? styles.cardHover : {}),
                  }}
                  onMouseEnter={() => setHovered(product.productID)}
                  onMouseLeave={() => setHovered(null)}
                >
                  <h1 style={styles.title}>
                    {product.productName || "Unnamed"}
                  </h1>

                  <h4 style={styles.body}>
                    {"Serial Number: " + product.productID || "N/A"}
                  </h4>

                  <p style={styles.body}>
                    Stock: {product.productCount || "N/A"}
                  </p>
                  <button
                    type="button"
                    style={{
                      ...styles.button,
                      ...(isBtnHovered ? styles.buttonHover : {}),
                    }}
                    onMouseEnter={() => setBtnHover(product.productID)}
                    onMouseLeave={() => setBtnHover(null)}
                    onClick={() => handleClickDelete(product.productID)}
                  >
                    Delete Product
                  </button>
                  <br/>
                  <br/>
                  <button
                    type="button"
                    style={{
                      ...styles.button,
                      ...(isBtnHovered ? styles.buttonHover : {}),
                    }}
                    onMouseEnter={() => setBtnHover(product.productID)}
                    onMouseLeave={() => setBtnHover(null)}
                    onClick={() => handleClick(product.productID)}
                  >
                    View Details
                  </button>
                </article>
              );
            })
          )}
        </section>
      </div>
    </main>
  );
}
