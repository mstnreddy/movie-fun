package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.springframework.orm.jpa.vendor.Database.MYSQL;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }



    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials() {
        DatabaseServiceCredentials databaseServiceCredential = new DatabaseServiceCredentials(System.getenv("VCAP_SERVICES"));

        return databaseServiceCredential;
    }

    @Bean
    @Qualifier("albums-dataSource")
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariDataSource hs=new HikariDataSource();
        hs.setDataSource(dataSource);
        return hs;
    }

    @Bean
    @Qualifier("movies-dataSource")
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariDataSource hs=new HikariDataSource();
        hs.setDataSource(dataSource);
        return hs;
    }

    @Bean
    public HibernateJpaVendorAdapter gethibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setDatabase(MYSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    @Qualifier("movies-entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getMoviesLocalContainerEntityManagerFactoryBean(@Qualifier("movies-dataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean lcemFact=new LocalContainerEntityManagerFactoryBean();
        lcemFact.setDataSource(dataSource);
        lcemFact.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        lcemFact.setPackagesToScan("org.superbiz.moviefun.movies");
        lcemFact.setPersistenceUnitName("movies");
        return lcemFact;
    }

    @Bean
    @Qualifier("albums-entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getAlbumsLocalContainerEntityManagerFactoryBean(@Qualifier("albums-dataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean lcemFact=new LocalContainerEntityManagerFactoryBean();
        lcemFact.setDataSource(dataSource);
        lcemFact.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        lcemFact.setPackagesToScan("org.superbiz.moviefun.albums");
        lcemFact.setPersistenceUnitName("albums");
        return lcemFact;
    }

    @Bean(name = "moviestransactionManager")
    public PlatformTransactionManager getMoviesTransactionManager(
            @Qualifier("movies-entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "albumstransactionManager")
    public PlatformTransactionManager getAlbumsTransactionManager(
            @Qualifier("albums-entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }


}
