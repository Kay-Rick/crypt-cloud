/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-31 13:47:42
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 22:46:44
 * @Description: 使用MockMvc测试Controller层
 */
package com.rick.cryptcloud.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class ExceptionTest {
    
    @Autowired
    MockMvc mockMvc;

    @Test
    public void testHello() throws Exception {

        /*
         * 1、mockMvc.perform执行一个请求。
         * 2、MockMvcRequestBuilders.get("XXX")构造一个请求。
         * 3、ResultActions.param添加请求传值
         * 4、ResultActions.accept(MediaType.TEXT_HTML_VALUE))设置返回类型
         * 5、ResultActions.andExpect添加执行完成后的断言。
         * 6、ResultActions.andDo添加一个结果处理器，表示要对结果做点什么事情
         *   比如此处使用MockMvcResultHandlers.print()输出整个响应结果信息。
         * 7、ResultActions.andReturn表示执行完成后返回相应的结果。
         */
        mockMvc.perform(MockMvcRequestBuilders
                .get("/mockmvc")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("username", "Tom"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello Tom"))
                .andDo(MockMvcResultHandlers.print());
    }
/*

    @Test
    void should_return_400_if_param_not_valid() throws Exception {
        mockMvc.perform(get("/api/illegalArgumentException")).andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("参数错误!"));
    }
*/
/*
    @Test
    void should_return_404_if_resourse_not_found() throws Exception {
        mockMvc.perform(get("/api/resourceNotFoundException")).andExpect(status().is(404))
                .andExpect(jsonPath("$.message").value("Sorry, the resourse not found!"));
    }
    */
}
