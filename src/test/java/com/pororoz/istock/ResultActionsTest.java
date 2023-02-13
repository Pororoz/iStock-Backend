package com.pororoz.istock;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;

public abstract class ResultActionsTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  protected Object asParsedJson(Object obj) throws JsonProcessingException {
    String json = new ObjectMapper().writeValueAsString(obj);
    return JsonPath.read(json, "$");
  }

  protected ResultActions getResultActions(String uri, HttpMethod httpMethod,
      MultiValueMap<String, String> params, Object object) throws Exception {
    if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
      throw new RuntimeException("GET과 DELETE는 body를 입력할 수 없습니다.");
    }
    MockHttpServletRequestBuilder buildersMethod = getMockMvcRequestBuildersMethod(
        uri, httpMethod);
    return mockMvc.perform(buildersMethod.contentType(MediaType.APPLICATION_JSON)
        .params(params).content(objectMapper.writeValueAsString(object)).with(csrf()));
  }

  protected ResultActions getResultActions(String uri, HttpMethod httpMethod,
      MultiValueMap<String, String> params) throws Exception {
    MockHttpServletRequestBuilder buildersMethod = getMockMvcRequestBuildersMethod(
        uri, httpMethod);
    return mockMvc.perform(buildersMethod.contentType(MediaType.APPLICATION_JSON)
        .params(params).with(csrf()));
  }

  protected ResultActions getResultActions(String uri, HttpMethod httpMethod, Object object)
      throws Exception {
    if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
      throw new RuntimeException("GET과 DELETE는 body를 입력할 수 없습니다.");
    }
    MockHttpServletRequestBuilder buildersMethod = getMockMvcRequestBuildersMethod(
        uri, httpMethod);
    return mockMvc.perform(buildersMethod.contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(object)).with(csrf()));
  }

  protected ResultActions getResultActions(String uri, HttpMethod httpMethod) throws Exception {
    MockHttpServletRequestBuilder buildersMethod = getMockMvcRequestBuildersMethod(
        uri, httpMethod);
    return mockMvc.perform(buildersMethod.contentType(MediaType.APPLICATION_JSON)
        .with(csrf()));
  }

  private MockHttpServletRequestBuilder getMockMvcRequestBuildersMethod(
      String uri, HttpMethod httpMethod) {
    if (httpMethod == HttpMethod.POST) {
      return post(uri);
    } else if (httpMethod == HttpMethod.PUT) {
      return put(uri);
    } else if (httpMethod == HttpMethod.GET) {
      return get(uri);
    } else if (httpMethod == HttpMethod.DELETE) {
      return delete(uri);
    } else if (httpMethod == HttpMethod.PATCH) {
      return patch(uri);
    } else {
      throw new RuntimeException("HttpMethod를 입력해주세요.");
    }
  }
}
