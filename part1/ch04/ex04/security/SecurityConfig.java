package tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 자바 설정 정보로 만듦
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     *  WebSecurityConfigurerAdapter 는 deprecate 됐지만 교재 실습을 위해서 임시적으로 사용
     *  SpringSecurity 는 따로 저장소로 만들 예정이기 때문에 부득이 하게 여기서만 사용하겠음.
     * */

    /**
     * 필드 주입은 스프링 설정을 목적으로 하는 @Configuration 같은 곳에서만 특별한 용도로 사용
     */
    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .authorizeRequests()
           .antMatchers("/design","/orders").access("hasRole('ROLE_USER')")
           .antMatchers("/","/**").access("permitAll")
           .and()
           .formLogin().loginPage("/login")
           .and()
           .logout().logoutSuccessUrl("/")
           .and().csrf();
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }
}
