package com.group7.hms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Component
@PropertySource("classpath:application.properties")
/*@EnableTransactionManagement*/
public class DatabaseConfig {


/*	@Value("${spring.datasource.driver-class}")*/
	/*  private String DB_DRIVER;*/
	  
	  @Value("${datasource.password}")
	  private String dbPassword;
	  
	  @Value("${datasource.url}")
	  private String dburl;
	  
	  @Value("${datasource.username}")
	  private String dbusername;

	/*	public String getDB_DRIVER() {
			return DB_DRIVER;
		}

		public void setDB_DRIVER(String dB_DRIVER) {
			this.DB_DRIVER = dB_DRIVER;
		}*/
		 
	
		
		@Override
		public String toString() {
			return "DatabaseConfig [DB_PASSWORD=" + dbPassword + ", DB_URL=" + dburl
					+ ", DB_USERNAME=" + dbusername + "]";
		}

		public String getDbPassword() {
			return dbPassword;
		}

		public void setDbPassword(String dbPassword) {
			this.dbPassword = dbPassword;
		}

		public String getDburl() {
			return dburl;
		}

		public void setDburl(String dburl) {
			this.dburl = dburl;
		}

		public String getDbusername() {
			return dbusername;
		}

		public void setDbusername(String dbusername) {
			this.dbusername = dbusername;
		}

	
}
