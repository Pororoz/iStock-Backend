package com.pororoz.istock.common.service;

import com.google.common.base.CaseFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleanup implements InitializingBean {

  @PersistenceContext
  private EntityManager entityManager;

  private List<String> tableNames;

  @Override
  public void afterPropertiesSet() {
    tableNames = entityManager.getMetamodel().getEntities().stream()
        .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
        .map(e -> camelToSnake(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, e.getName())))
        .collect(Collectors.toList());
  }

  private String camelToSnake(String str) {
    char c = str.charAt(0);
    StringBuilder result = new StringBuilder(String.valueOf(Character.toLowerCase(c)));

    for (int i = 1; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (Character.isUpperCase(ch)) {
        result.append('_');
        result.append(Character.toLowerCase(ch));
      } else {
        result.append(ch);
      }
    }
    return result.toString();
  }

  @Transactional
  public void execute() {
    entityManager.flush();
    entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

    for (String tableName : tableNames) {
      // role 테이블만 제외하고 데이터베이스 clean 하기!!
      if (tableName.equals("role")) {
        continue;
      }
      entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
      entityManager.createNativeQuery("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1")
          .executeUpdate();
    }

    entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
  }
}
