<script setup lang="ts">
import { Button, Form, FormItem, Input } from 'ant-design-vue'
</script>

<template>
  <div
    class="flex min-h-screen w-full items-center justify-center bg-[url(@/assets/login/bg.png)] bg-cover"
  >
    <div class="flex w-full max-w-396.25 items-start justify-between gap-5">
      <div class="flex flex-col items-center gap-8.75 pt-3.5">
        <h1
          class="bg-linear-180 from-[#38A8FF] to-[#0066FF] bg-clip-text text-[44px]/11 font-bold text-transparent"
        >
          智慧校园 · 登录即启程
        </h1>
        <span class="text-[27px]/9.5 text-[#707A89]">一站式学习与生活助手，开启高效校园新体验</span>
        <img src="@/assets/login/view.png" alt="view" class="w-full max-w-232.5 object-cover" />
      </div>
      <div class="w-full max-w-140 rounded-3xl bg-white px-13.75 pt-22.5 pb-20">
        <h2 class="text-center text-3xl/10.5 font-semibold text-[#333333]">重庆工程学院智能平台</h2>
        <div class="mt-26.25 flex flex-col">
          <h3 class="text-2xl font-medium text-[#333333]">登录</h3>
          <span class="mt-1.25 mb-5 text-sm text-[#969799]">如未验证，验证后自动登录</span>
          <Form>
            <FormItem name="phone">
              <Input placeholder="请输入手机号" class="h-14.5 rounded-lg! px-5! text-base!" />
            </FormItem>
            <FormItem name="password">
              <Input
                placeholder="请输入密码"
                type="password"
                class="h-14.5 rounded-lg! px-5! text-base!"
              />
            </FormItem>
            <FormItem>
              <Button
                type="primary"
                html-type="submit"
                class="h-14.5! w-full rounded-lg! text-base!"
                >注册/登录</Button
              >
            </FormItem>
          </Form>
        </div>
        <div class="flex items-center justify-center gap-[.5em] text-sm text-[#969799]">
          <span class="cursor-pointer hover:underline">忘记密码</span>|<span
            class="cursor-pointer hover:underline"
            >使用帮助</span
          >
        </div>
      </div>
    </div>
  </div>
</template>
