import React from 'react';
import Icon from '@ant-design/icons';
import type { GetProps } from 'antd';

type CustomIconComponentProps = GetProps<typeof Icon>;

const PlaySvg = () => (
    <svg width="16px" height="16px" viewBox="0 0 16 16" version="1.1" xmlns="http://www.w3.org/2000/svg">
        <g stroke="none" strokeWidth="1" fill="none" fillRule="evenodd">
            <g transform="translate(-450, -289)">
                <g  transform="translate(450, 289)">
                    <rect  x="0" y="0" width="16" height="16"></rect>
                    <g>
                        <circle fill="#0066FF" cx="8" cy="8" r="8"></circle>
                        <rect  fill="#FFFFFF" x="5" y="5" width="6" height="6" rx="1"></rect>
                    </g>
                </g>
            </g>
        </g>
    </svg>
);


const PlaySvgIcon = (props: Partial<CustomIconComponentProps>) => (
    <Icon component={PlaySvg} {...props} />
);

const CustomPlayIcon: React.FC = () => (
    <PlaySvgIcon />
);

export default CustomPlayIcon;
