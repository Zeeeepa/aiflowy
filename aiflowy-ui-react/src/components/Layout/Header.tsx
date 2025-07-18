import React, {useState} from 'react';
import {Avatar, Button, Dropdown, MenuProps, Space} from 'antd';
import styles from "./layout.module.less";
import {
    ExportOutlined, KeyOutlined, MenuFoldOutlined, MenuUnfoldOutlined, MoreOutlined, SettingOutlined,
    UserOutlined
} from "@ant-design/icons";
import {Header as AntdHeader} from "antd/es/layout/layout";

import 'dayjs/locale/zh-cn';
import AiChatModal from "../AiChatModal";
import {useLogout} from "../../hooks/useLogout.ts";
import {useAppStore} from "../../store/appStore.ts";
import UpdatePassword from "./UpdatePassword.tsx";
import Settings from "./Settings.tsx";
import {useLayout} from "../../hooks/useLayout.tsx";
import rightTopBotIcon from "../../assets/rightTopBot.png"

type HeaderProps = {
    collapsed: boolean,
}


const Header: React.FC<HeaderProps> = ({collapsed}) => {
    const [openUpdatePassword, setOpenUpdatePassword] = useState(false)
    const [openSettings, setOpenSettings] = useState(false)
    const [openAiChatModal, setOpenAIChatModal] = useState(false)
    const {doLogout} = useLogout();
    const store = useAppStore();
    const {options, setOptions} = useLayout();

    const avatarItems: MenuProps['items'] = [
        {
            key: '1',
            label: <><KeyOutlined/> 修改密码</>,
            onClick: () => setOpenUpdatePassword(true)
        },
        {
            key: '2',
            label: <> <SettingOutlined/> 个人设置</>,
            onClick: () => setOpenSettings(true)
        },
        {
            type: 'divider',
        },
        {
            key: 'loggedOut',
            label: <>  <ExportOutlined/> 退出登录</>,
            onClick: doLogout
        },
    ];

    return (
        <AntdHeader className={styles.header}>

            <UpdatePassword open={openUpdatePassword} onClose={() => setOpenUpdatePassword(false)}/>
            <Settings open={openSettings} onClose={() => setOpenSettings(false)}/>
            <AiChatModal open={openAiChatModal} onClose={() => setOpenAIChatModal(false)}/>

            <div style={{display: "flex"}}>


                {options?.showLeftMenu && <Button
                    type="text"
                    icon={collapsed ? <MenuUnfoldOutlined/> : <MenuFoldOutlined/>}
                    onClick={() => setOptions({
                        leftMenuCollapsed: !collapsed,
                    })}
                    style={{
                        fontSize: '16px',
                        width: 48,
                        height: 48,
                    }}
                />}

                {options?.headerLeftEl}

                <div style={{display: "flex", marginLeft: "auto", justifyContent: "end"}}>

                    <Space align={"center"} size={"large"}
                           style={{textAlign: "right", color: "#fff"}}>

                        <Avatar src={rightTopBotIcon} style={{width: "40px", height: "40px", cursor: "pointer"}} onClick={()=>{
                            setOpenAIChatModal(!openAiChatModal)
                        }}/>


                        <Dropdown menu={{items: avatarItems}} placement="bottomRight">
                            <div style={{fontSize: "14px", color: "#333", cursor: "pointer"}}>
                                {store.avatar ?
                                    <img src={store.avatar} alt="" style={{width: "32px", borderRadius: "50%"}}/> :
                                    <Avatar icon={<UserOutlined/>}/>} {store.nickName}
                            </div>
                        </Dropdown>

                        <MoreOutlined style={{fontSize: "18px", color: "#333", cursor: "pointer"}}/>
                    </Space>
                </div>
            </div>
        </AntdHeader>
    );
};


export default Header;