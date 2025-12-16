package com.meli.social;

import org.springframework.boot.SpringApplication;

public class TestSocialApplication {

	public static void main(String[] args) {
		SpringApplication.from(SocialApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
