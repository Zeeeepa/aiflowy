import React from 'react';
import welcome from "../../assets/welcome.png";


const Welcome: React.FC = () => {


    return (
        <div style={{background: "#fff", textAlign: "center", padding: "30px"}}>
            <h1>Hi~ 尊敬的用户</h1>
            <h3>欢迎使用 AIFlowy 快速构建您的 AI 产品！</h3>
            <img src={welcome} alt="" style={{width: "60%"}}/>
        </div>
    )
};

export default {
    path: "index",
    element: <Welcome/>
};
