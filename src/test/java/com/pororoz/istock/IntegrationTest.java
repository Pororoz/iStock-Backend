package com.pororoz.istock;

import com.pororoz.istock.common.service.DatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest extends ResultActionsTest {

  @Autowired
  protected DatabaseCleanup databaseCleanup;

  @AfterEach
  void clearDatabase() {
    databaseCleanup.execute();
  }
}
