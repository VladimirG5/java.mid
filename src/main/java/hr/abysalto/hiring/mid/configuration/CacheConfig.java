package hr.abysalto.hiring.mid.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@Configuration
@EnableCaching
@EnableJdbcAuditing
public class CacheConfig {
}

