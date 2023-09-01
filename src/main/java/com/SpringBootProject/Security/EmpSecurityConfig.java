package com.SpringBootProject.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class EmpSecurityConfig {

   /* @Bean // JDBC schema provider by spring boot .This allows us to use the users defined in the database and prevent hard coding.
    UserDetailsManager userDetailsManager(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    } */

    @Bean 
    UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        // Pointing the users table to our custom table - members
        jdbcUserDetailsManager.setUsersByUsernameQuery("select * from members where user_id=?");

        // Pointing the authorities table to our custom table - roles
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select * from roles where user_id=?");

        return jdbcUserDetailsManager;
    }

    @Bean // Restricting access of end points according to respective roles
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests( configurer ->
                configurer.requestMatchers(HttpMethod.GET, "/api/employees").hasRole("EMPLOYEE")
                          .requestMatchers(HttpMethod.GET, "/api/employees/**").hasRole("EMPLOYEE")
                          .requestMatchers(HttpMethod.POST, "/api/employees").hasRole("MANAGER")
                          .requestMatchers(HttpMethod.PUT, "/api/employees").hasRole("MANAGER")
                          .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
        );
        http.httpBasic(Customizer.withDefaults()); //http basic authentication

        // disable Cross Site Request Forgery , not required for stateless Rest APIs
        http.csrf(csrf->csrf.disable());

       return http.build();
    }

// Hard-Coded user roles - Username and Password (Without Database)
   /* @Bean // Assigning roles to different users
    public InMemoryUserDetailsManager userDetailsManager(){
        UserDetails Karan= User.builder()
                .username("karan")
                .password("{noop}johar")
                .roles("EMPLOYEE")
                .build();

        UserDetails Harry= User.builder()
                .username("harry")
                .password("{noop}potter")
                .roles("EMPLOYEE", "MANAGER")
                .build();

        UserDetails Paras= User.builder()
                .username("paras")
                .password("{noop}dongre")
                .roles("EMPLOYEE","MANAGER","ADMIN")
                .build();

                return new InMemoryUserDetailsManager(Karan, Harry, Paras);
    } */

}
