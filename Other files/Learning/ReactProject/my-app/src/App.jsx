import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import UpdateProduct from "./components/UpdateProduct";
import './App.css'
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ProductDetails from "./components/ProductDetails";
import FirstPage from './components/FirstPage';
import AddProduct from './components/AddProduct';
import Navigation from './Just_study/NavBar';
import { createBrowserRouter,RouterProvider } from 'react-router-dom';
import Homie from './Just_study/homie';
import Jing from './Just_study/jing';
import { createContext } from 'react';
// import { UNSAFE_DataRouterContext } from 'react-router-dom';
// export const variable = createContext();
const route = createBrowserRouter([
    {
        path : "/products/:id",
        element : <ProductDetails/>
    },
    {
        path : "/add-product",
        element : <AddProduct/>
    },
    {
        path : "/",
        element : <FirstPage/>
    },
    {
        path : "/update-product/:productid",
        element : <UpdateProduct/>
    }
]);
export default function App() {
  return <RouterProvider router={route} />
    // <Router>
    //   <Routes>
    //     <Route path="/" element={<FirstPage />} />
    //     <Route path="/products/:id" element={<ProductDetails />} />
    //     <Route path='/add-product' element={<AddProduct/>} />
    //     <Route path="/update-product/:productid" element={<UpdateProduct />} />
    //   </Routes>
    // </Router>

    
  // );
//   let fang = "giga";
//   return (
    
//     // <RouterProvider router={route} />

    
//     <variable.Provider value={fang}>
// <Navigation/>
//     </variable.Provider>
//   );
}




