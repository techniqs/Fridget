package at.ac.tuwien.sepm.fridget.server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()

            .authorizeRequests()

            // Public routes:
            .antMatchers("/").permitAll()
            .antMatchers("/greeting/World").permitAll()
            .antMatchers("/user/login").permitAll()
            .antMatchers("/user/create").permitAll()
            .antMatchers("/user/forgot-password").permitAll()
            .antMatchers("/user/verify-reset-code").permitAll()
            .antMatchers("/user/reset-password").permitAll()

            // All other routes have to be authenticated:
            .anyRequest().authenticated()

            .and().httpBasic()

            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

}
