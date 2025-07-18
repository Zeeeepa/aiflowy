import React from 'react';
import Icon from '@ant-design/icons';
import type { GetProps } from 'antd';

type CustomIconComponentProps = GetProps<typeof Icon>;

const CopySvg = () => (
    <svg width="13.5px" height="13.5px" viewBox="0 0.2 13.5 13.5" version="1.1" xmlns="http://www.w3.org/2000/svg">
        <g id="页面-1" stroke="none" strokeWidth="1" fill="none" fillRule="evenodd">
            <g  transform="translate(-507, -290.5)">
                <g  transform="translate(506, 289)">
                    <rect  x="0" y="0" width="16" height="16"></rect>
                    <g transform="translate(1, 2)" stroke="#969799">
                        <path d="M3,2.83814688 L3,2.83814688 L3,2 C3,0.8954305 3.8954305,0 5,0 L11,0 C12.1045695,0 13,0.8954305 13,2 L13,8 C13,9.1045695 12.1045695,10 11,10 L10.4182681,10 L10.4182681,10" id="路径"></path>
                        <rect  x="0.5" y="2.5" width="10" height="10" rx="2"></rect>
                    </g>
                </g>
            </g>
        </g>
    </svg>
);


const CopySvgIcon = (props: Partial<CustomIconComponentProps>) => (
    <Icon component={CopySvg} {...props} />
);

const CustomCopyIcon: React.FC = () => (
    <CopySvgIcon />
);

export default CustomCopyIcon;
