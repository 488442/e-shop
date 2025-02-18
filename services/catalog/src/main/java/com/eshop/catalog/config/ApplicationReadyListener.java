package com.eshop.catalog.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationReadyListener.class);

  @Override
  public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
    logger.info("Catalog service is up and running...");
  }
}
