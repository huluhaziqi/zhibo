package com.lin.MyZhibo.test;

import com.lin.MyTest.TestSvServer;
import com.lin.MyTest.enums.VideoTypeEnum;
import com.lin.MyTest.model.request.TestCreateRequest;
import com.lin.MyTest.model.request.TestUpdateRequest;
import com.lin.MyTest.util.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TestSvServer.class)
@AutoConfigureMockMvc
@ActiveProfiles("Test")
public class TestControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private static final Logger logger = LoggerFactory.getLogger(TestControllerTest.class);

    private String str = "";
    private MockMvc mvc;
    private MockHttpSession session;

    @Before
    public void init(){
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();
    }

    private Integer pageNumber = 1;
    private Integer pageSize = 50;

    @Test
    public void create() throws Exception{
        TestCreateRequest request = new TestCreateRequest();
        request.setUserId(144007325L);
        request.setVideoType(VideoTypeEnum.Test.getValue());
        request.setImagePath(("/live/defaultJpg220.jpg"));
        request.setType((byte)1);
        request.setTitle("测试xxxx创建");
        request.setCityId(1);
        request.setTagId(1);
        request.setBuildingId("7302");
        request.setScheduledTime(new Date());
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/Tests/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.obj2JsonStr(request))
                .header("X-xxxPassport-UserId", "144007325")
                .header("X-xxxPassport-UId", "144007325")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //打印所有结果
                .andDo(print())
                .andReturn();
        logger.info("create result {}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void update() throws Exception{
        TestUpdateRequest request = new TestUpdateRequest();
        request.setImagePath(("/live/defaultJpg221.jpg"));
        request.setTitle("测试xxxx创建");
        request.setCityId(2);
        request.setBuildingId("7302");
        request.setScheduledTime(new Date().getTime());
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/Tests/{id}/update",12001)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.obj2JsonStr(request))
                .header("X-xxxPassport-UserId", "144007325")
                .header("X-xxxPassport-UId", "144007325")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //打印所有结果
                .andDo(print())
                .andReturn();
        logger.info("update result {}", mvcResult.getResponse().getContentAsString());
    }
}