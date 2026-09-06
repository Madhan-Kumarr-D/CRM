import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { createBrowserRouter,RouterProvider } from 'react-router-dom'
import Homie from './Just_study/homie.jsx'
import Jing from './Just_study/jing.jsx'
import ProductDetails from './components/ProductDetails.jsx'
import AddProduct from './components/UpdateProduct.jsx'
import FirstPage from './components/FirstPage.jsx'
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
    }
]);
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
    {/* <RouterProvider router={route} /> */}
  </StrictMode>,
)
