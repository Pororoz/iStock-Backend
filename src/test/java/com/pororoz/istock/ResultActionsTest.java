package com.pororoz.istock;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
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


  protected ResultActions getResultActionsForGetMethod(String url,
      MultiValueMap<String, String> params) throws Exception {
    return mockMvc.perform(get(url)
        .params(params)
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf())
    );
  }

  protected ResultActions getResultActionsWithBody(Object request, HttpMethod httpMethod,
      String uri)
      throws Exception {
    MockHttpServletRequestBuilder buildersMethod = getMockMvcRequestBuildersMethod(
        httpMethod, URI.create(uri));
    return mockMvc.perform(buildersMethod
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .with(csrf()));
  }

  protected ResultActions getResultActionsWithNoBody(HttpMethod httpMethod,
      String uri)
      throws Exception {
    MockHttpServletRequestBuilder buildersMethod = getMockMvcRequestBuildersMethod(
        httpMethod, URI.create(uri));
    return mockMvc.perform(buildersMethod
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf()));
  }

  private MockHttpServletRequestBuilder getMockMvcRequestBuildersMethod(HttpMethod httpMethod,
      URI uri) {
    if (httpMethod == HttpMethod.POST) {
      return post(uri);
    } else if (httpMethod == HttpMethod.PUT) {
      return put(uri);
    } else if (httpMethod == HttpMethod.DELETE) {
      return delete(uri);
    } else if (httpMethod == HttpMethod.PATCH) {
      return patch(uri);
    } else {
      throw new RuntimeException();
    }
  }
}
