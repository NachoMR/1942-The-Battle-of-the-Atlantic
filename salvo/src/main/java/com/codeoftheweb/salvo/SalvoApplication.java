package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	//There is another way to define this @Bean:
	//public PasswordEncoder passwordEncoder() {
	// return new BCryptPasswordEncoder();
	// }

	@Bean
	public CommandLineRunner init(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			Player p1 = new Player("Jack", "Bauer", "j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player p2 = new Player("Chloe", "O'Brian", "c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player p3 = new Player("Kim", "Bauer", "kim_bauer@gmail.com", passwordEncoder().encode("kb"));
			Player p4 = new Player("Tony", "Almeida", "t.almeida@ctu.gov", passwordEncoder().encode("mole"));
			Game g1 = new Game(LocalDateTime.now());
			Game g2 = new Game(LocalDateTime.of(2019, Month.FEBRUARY, 04, 14, 30));
			Game g3 = new Game(LocalDateTime.parse("2019-02-07T17:45:00"));
			Game g4 = new Game(LocalDateTime.parse("2019-01-02T17:45:00"));
			Game g5 = new Game(LocalDateTime.parse("2019-01-01T17:45:00"));
			Game g6 = new Game(LocalDateTime.parse("2019-02-02T17:45:00"));
			Game g7 = new Game(LocalDateTime.parse("2019-01-14T17:45:00"));
			Game g8 = new Game(LocalDateTime.parse("2019-01-30T17:45:00"));
			Game g9 = new Game(LocalDateTime.now());
			GamePlayer gp1 = new GamePlayer(g1, p1);
			GamePlayer gp2 = new GamePlayer(g1, p2);
			GamePlayer gp3 = new GamePlayer(g2, p1);
			GamePlayer gp4 = new GamePlayer(g2, p2);
			GamePlayer gp5 = new GamePlayer(g3, p2);
			GamePlayer gp6 = new GamePlayer(g3, p4);
			GamePlayer gp7 = new GamePlayer(g4, p2);
			GamePlayer gp8 = new GamePlayer(g4, p1);
			GamePlayer gp9 = new GamePlayer(g5, p4);
			GamePlayer gp10 = new GamePlayer(g5, p1);
			GamePlayer gp11 = new GamePlayer(g6, p3);
			GamePlayer gp12 = new GamePlayer(g7, p4);
			GamePlayer gp13 = new GamePlayer(g8, p3);
			GamePlayer gp14 = new GamePlayer(g8, p4);
			GamePlayer gp15 = new GamePlayer(g9, p4);
			GamePlayer gp16 = new GamePlayer(g9, p2);
			Ship s1 = new Ship("destroyer", Arrays.asList("H2", "H3", "H4"));
			Ship s2 = new Ship("submarine", Arrays.asList("E1", "F1", "G1"));
			Ship s3 = new Ship("patrol_boat", Arrays.asList("B4", "B5"));
			Ship s4 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship s5 = new Ship("patrol_boat", Arrays.asList("F1", "F2"));
			Ship s6 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship s7 = new Ship("patrol_boat", Arrays.asList("C6", "C7"));
			Ship s8 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"));
			Ship s9 = new Ship("patrol_boat", Arrays.asList("G6", "H6"));
			Ship s10 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship s11 = new Ship("patrol_boat", Arrays.asList("C6", "C7"));
			Ship s12 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"));
			Ship s13 = new Ship("patrol_boat", Arrays.asList("G6", "H6"));
			Ship s14 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship s15 = new Ship("patrol_boat", Arrays.asList("C6", "C7"));
			Ship s16 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"));
			Ship s17 = new Ship("patrol_boat", Arrays.asList("G6", "H6"));
			Ship s18 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship s19 = new Ship("patrol_boat", Arrays.asList("C6", "C7"));
			Ship s20 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"));
			Ship s21 = new Ship("patrol_boat", Arrays.asList("G6", "H6"));
			Ship s22 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship s23 = new Ship("patrol_boat", Arrays.asList("C6", "C7"));
			Ship s24 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"));
			Ship s25 = new Ship("patrol_boat", Arrays.asList("C6", "C7"));
			Ship s26 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"));
			Ship s27 = new Ship("patrol_boat", Arrays.asList("G6", "H6"));
			Ship s28 = new Ship("carrier", Arrays.asList("A6", "A7", "A8", "A9", "A10"));
			Ship s29 = new Ship("battleship", Arrays.asList("B6", "C6", "D6", "E6"));
			Ship s30 = new Ship("destroyer", Arrays.asList("B3", "C3", "D3"));
			Ship s31 = new Ship("submarine", Arrays.asList("E8", "E9", "E10"));
			Ship s32 = new Ship("patrol_boat", Arrays.asList("G6", "H6"));
			Ship s33 = new Ship("carrier", Arrays.asList("H6", "H7", "H8", "H9", "H10"));
			Ship s34 = new Ship("battleship", Arrays.asList("B1", "C1", "D1", "E1"));
			Ship s35 = new Ship("destroyer", Arrays.asList("B9", "C9", "D9"));
			Ship s36 = new Ship("submarine", Arrays.asList("D3", "D4", "D5"));
			Ship s37 = new Ship("patrol_boat", Arrays.asList("G4", "H4"));

			gp1.addShip(s1);
			gp1.addShip(s2);
			gp1.addShip(s3);
			gp2.addShip(s4);
			gp2.addShip(s5);
			gp3.addShip(s6);
			gp3.addShip(s7);
			gp4.addShip(s8);
			gp4.addShip(s9);
			gp5.addShip(s10);
			gp5.addShip(s11);
			gp6.addShip(s12);
			gp6.addShip(s13);
			gp7.addShip(s14);
			gp7.addShip(s15);
			gp8.addShip(s16);
			gp8.addShip(s17);
			gp9.addShip(s18);
			gp9.addShip(s19);
			gp10.addShip(s20);
			gp10.addShip(s21);
			gp11.addShip(s22);
			gp11.addShip(s23);
			gp13.addShip(s24);
			gp13.addShip(s25);
			gp14.addShip(s26);
			gp14.addShip(s27);
			gp15.addShip(s28);
			gp15.addShip(s29);
			gp15.addShip(s30);
			gp15.addShip(s31);
			gp15.addShip(s32);
			gp16.addShip(s33);
			gp16.addShip(s34);
			gp16.addShip(s35);
			gp16.addShip(s36);
			gp16.addShip(s37);

			Salvo sa1 = new Salvo(1, Arrays.asList("B5", "C5", "F1"));
			Salvo sa2 = new Salvo(2, Arrays.asList("F2", "D5"));
			Salvo sa3 = new Salvo(1, Arrays.asList("B4", "B5", "B6"));
			Salvo sa4 = new Salvo(2, Arrays.asList("E1", "H3", "A2"));
			Salvo sa5 = new Salvo(1, Arrays.asList("A2", "A4", "G6"));
			Salvo sa6 = new Salvo(2, Arrays.asList("A3", "H2"));
			Salvo sa7 = new Salvo(1, Arrays.asList("B5", "D5", "C7"));
			Salvo sa8 = new Salvo(2, Arrays.asList("C5", "C6"));
			Salvo sa9 = new Salvo(1, Arrays.asList("G6", "H6", "A4"));
			Salvo sa10 = new Salvo(2, Arrays.asList("A2", "A3", "D8"));
			Salvo sa11 = new Salvo(1, Arrays.asList("H1", "H2", "H3"));
			Salvo sa12 = new Salvo(2, Arrays.asList("E1", "F2", "G3"));
			Salvo sa13 = new Salvo(1, Arrays.asList("A3", "A4", "F7"));
			Salvo sa14 = new Salvo(2, Arrays.asList("A2", "G6", "H6"));
			Salvo sa15 = new Salvo(1, Arrays.asList("B5", "C6", "H1"));
			Salvo sa16 = new Salvo(2, Arrays.asList("C5", "C7", "D5"));
			Salvo sa17 = new Salvo(1, Arrays.asList("A1", "A2", "A3"));
			Salvo sa18 = new Salvo(2, Arrays.asList("G6", "G7", "G8"));
			Salvo sa19 = new Salvo(1, Arrays.asList("B5", "B6", "C7"));
			Salvo sa20 = new Salvo(2, Arrays.asList("C6", "D6", "E6"));
			Salvo sa21 = new Salvo(3, Arrays.asList("H1", "H8"));

			Salvo sa22 = new Salvo(1, Arrays.asList("G6", "G7", "G8", "G9", "G10"));
			Salvo sa23 = new Salvo(2, Arrays.asList("B1", "C1", "D1", "E1"));
			Salvo sa24 = new Salvo(3, Arrays.asList("D3", "D4", "D5"));
			Salvo sa25 = new Salvo(4, Arrays.asList("A4","A6", "A8"));
			Salvo sa26 = new Salvo(5, Arrays.asList("C7", "D7", "E7", "F7"));
			Salvo sa27 = new Salvo(6, Arrays.asList("B9", "C9", "D9"));
			Salvo sa28 = new Salvo(7, Arrays.asList("H1", "J8"));
			Salvo sa29 = new Salvo(8, Arrays.asList("G4", "H4"));

			Salvo sa30 = new Salvo(1, Arrays.asList("A6", "A7", "A8", "A9", "A10"));
			Salvo sa31 = new Salvo(1, Arrays.asList("I6", "I7", "I8", "I9", "I10"));
			Salvo sa32 = new Salvo(3, Arrays.asList("B6", "C6", "D6", "E6"));
			Salvo sa33 = new Salvo(4, Arrays.asList("B2", "C3", "D4"));
			Salvo sa34 = new Salvo(5, Arrays.asList("J1", "H8"));
			Salvo sa35 = new Salvo(6, Arrays.asList("E8", "F8", "G8"));
			Salvo sa36 = new Salvo(7, Arrays.asList("E9", "E10", "F2"));
			Salvo sa37 = new Salvo(8, Arrays.asList("G6", "H6"));

			gp1.addSalvo(sa1);
			gp1.addSalvo(sa2);
			gp2.addSalvo(sa3);
			gp2.addSalvo(sa4);
			gp3.addSalvo(sa5);
			gp3.addSalvo(sa6);
			gp4.addSalvo(sa7);
			gp4.addSalvo(sa8);
			gp5.addSalvo(sa9);
			gp5.addSalvo(sa10);
			gp6.addSalvo(sa11);
			gp6.addSalvo(sa12);
			gp7.addSalvo(sa13);
			gp7.addSalvo(sa14);
			gp8.addSalvo(sa15);
			gp8.addSalvo(sa16);
			gp9.addSalvo(sa17);
			gp9.addSalvo(sa18);
			gp10.addSalvo(sa19);
			gp10.addSalvo(sa20);
			gp10.addSalvo(sa21);

			gp15.addSalvo(sa22);
			gp15.addSalvo(sa23);
			gp15.addSalvo(sa24);
			gp15.addSalvo(sa25);
			gp15.addSalvo(sa26);
			gp15.addSalvo(sa27);
			gp15.addSalvo(sa28);
			gp15.addSalvo(sa29);
			gp16.addSalvo(sa30);
			gp16.addSalvo(sa31);
			gp16.addSalvo(sa32);
			gp16.addSalvo(sa33);
			gp16.addSalvo(sa34);
			gp16.addSalvo(sa35);
			gp16.addSalvo(sa36);
			gp16.addSalvo(sa37);


			Score sc1 = new Score(1.0, g1, p1);
			Score sc2 = new Score(0.0, g1, p2);
			Score sc3 = new Score(0.5, g2, p1);
			Score sc4 = new Score(0.5, g2, p2);
			Score sc5 = new Score(1.0, g3, p2);
			Score sc6 = new Score(0.0, g3, p4);
			Score sc7 = new Score(0.5, g4, p2);
			Score sc8 = new Score(0.5, g4, p1);
//			Score sc9 = new Score(0.0, g9, p2);
//			Score sc10 = new Score(1.0, g9, p4);

			playerRepository.save(p1);
			playerRepository.save(p2);
			playerRepository.save(p3);
			playerRepository.save(p4);
			gameRepository.save(g1);
			gameRepository.save(g2);
			gameRepository.save(g3);
			gameRepository.save(g4);
			gameRepository.save(g5);
			gameRepository.save(g6);
			gameRepository.save(g7);
			gameRepository.save(g8);
			gameRepository.save(g9);

			gamePlayerRepository.save(gp1);
			gamePlayerRepository.save(gp2);
			gamePlayerRepository.save(gp3);
			gamePlayerRepository.save(gp4);
			gamePlayerRepository.save(gp5);
			gamePlayerRepository.save(gp6);
			gamePlayerRepository.save(gp7);
			gamePlayerRepository.save(gp8);
			gamePlayerRepository.save(gp9);
			gamePlayerRepository.save(gp10);
			gamePlayerRepository.save(gp11);
			gamePlayerRepository.save(gp12);
			gamePlayerRepository.save(gp13);
			gamePlayerRepository.save(gp14);
			gamePlayerRepository.save(gp15);
			gamePlayerRepository.save(gp16);

			shipRepository.save(s1);
			shipRepository.save(s2);
			shipRepository.save(s3);
			shipRepository.save(s4);
			shipRepository.save(s5);
			shipRepository.save(s6);
			shipRepository.save(s7);
			shipRepository.save(s8);
			shipRepository.save(s9);
			shipRepository.save(s10);
			shipRepository.save(s11);
			shipRepository.save(s12);
			shipRepository.save(s13);
			shipRepository.save(s14);
			shipRepository.save(s15);
			shipRepository.save(s16);
			shipRepository.save(s17);
			shipRepository.save(s18);
			shipRepository.save(s19);
			shipRepository.save(s20);
			shipRepository.save(s21);
			shipRepository.save(s22);
			shipRepository.save(s23);
			shipRepository.save(s24);
			shipRepository.save(s25);
			shipRepository.save(s26);
			shipRepository.save(s27);
			shipRepository.save(s28);
			shipRepository.save(s29);
			shipRepository.save(s30);
			shipRepository.save(s31);
			shipRepository.save(s32);
			shipRepository.save(s33);
			shipRepository.save(s34);
			shipRepository.save(s35);
			shipRepository.save(s36);
			shipRepository.save(s37);

			salvoRepository.save(sa1);
			salvoRepository.save(sa2);
			salvoRepository.save(sa3);
			salvoRepository.save(sa4);
			salvoRepository.save(sa5);
			salvoRepository.save(sa6);
			salvoRepository.save(sa7);
			salvoRepository.save(sa8);
			salvoRepository.save(sa9);
			salvoRepository.save(sa10);
			salvoRepository.save(sa11);
			salvoRepository.save(sa12);
			salvoRepository.save(sa13);
			salvoRepository.save(sa14);
			salvoRepository.save(sa15);
			salvoRepository.save(sa16);
			salvoRepository.save(sa17);
			salvoRepository.save(sa18);
			salvoRepository.save(sa19);
			salvoRepository.save(sa20);
			salvoRepository.save(sa21);
			salvoRepository.save(sa22);
			salvoRepository.save(sa23);
			salvoRepository.save(sa24);
			salvoRepository.save(sa25);
			salvoRepository.save(sa26);
			salvoRepository.save(sa27);
			salvoRepository.save(sa28);
			salvoRepository.save(sa29);
			salvoRepository.save(sa29);
			salvoRepository.save(sa30);
			salvoRepository.save(sa31);
			salvoRepository.save(sa32);
			salvoRepository.save(sa33);
			salvoRepository.save(sa34);
			salvoRepository.save(sa35);
			salvoRepository.save(sa36);
			salvoRepository.save(sa37);

			scoreRepository.save(sc1);
			scoreRepository.save(sc2);
			scoreRepository.save(sc3);
			scoreRepository.save(sc4);
			scoreRepository.save(sc5);
			scoreRepository.save(sc6);
			scoreRepository.save(sc7);
			scoreRepository.save(sc8);
//			scoreRepository.save(sc9);
//			scoreRepository.save(sc10);
		};
	}


}

//Configuring Names and Passwords (only one class can be public within the same file, therefore the below subclass should not be public!):
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	private PlayerRepository playerRepo;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName -> {
			Player player = playerRepo.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
						//AuthorityUtils.createAuthorityList("ADMIN"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

//Enabling URL Access to Authenticated Users above (only one class can be public within the same file, therefore the below subclass should not be public!):
@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				//.antMatchers("/admin/**").hasAuthority("ADMIN")
				.antMatchers("api/game_view/**").hasAuthority("USER");
				//.antMatchers("/users/**").hasRole("USER")  //USER role can access /users/**
				//.antMatchers("/admin/**").hasRole("ADMIN")  //ADMIN role can access /admin/**
				//.antMatchers("/quests/**").permitAll()  // anyone can access /quests/**
				//.anyRequest().authenticated()  //any other request just need authentication
				//now we can use the and() to concatenate sections or simply start a new one with: http.formLogin()... as shown below.
				//.and()
				//.formLogin()
		 				//.usernameParameter("username")
						//.passwordParameter("password")
						//.loginPage("/api/login");

		http.formLogin()
				.usernameParameter("userName")
				.passwordParameter("password")
				.loginPage("/api/login");
				//The default URL where the Spring Login will POST to trigger the authentication process is /login. It can be overwritten using .loginProcessingUrl()
				//.loginProcessingUrl("/perform_login")
				//.defaultSuccessUrl("/homepage.html", true)
				//.failureUrl("/login.html?error=true")
				//.failureHandler(authenticationFailureHandler())

		http.logout().logoutUrl("/api/logout");
		//.deleteCookies("JSESSIONID")
		//.logoutSuccessHandler(logoutSuccessHandler())

		//If you have an X-frame error you only need to add this line to your WebSecurityConfig class.
		//So we need to add the below line of code to be able to see the H2-console in our database.
		http.headers().frameOptions().disable();

		//Be sure to include your login URL in the list of URLs accessible to users who are not logged in!
		//Don't forget to override the default settings that send HTML forms when unauthenticated access happens and when someone logs in or out.
		//Be sure to include your login URL in the list of URLs accessible to users who are not logged in!
		//Don't forget to override the default settings that send HTML forms when unauthenticated access happens and when someone logs in or out.


		//See the Resources for example code. Be sure to follow the example for web services. You want Spring
		// to just sent HTTP success and response codes, no HTML pages.

		// turn off checking for CSRF tokens.CSRF tokens are disabled because supporting them requires a bit of work, and
		// this kind of attack is more typical with regular web page browsing.
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
