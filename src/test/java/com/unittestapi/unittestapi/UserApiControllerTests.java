package com.unittestapi.unittestapi;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unittestapi.unittestapi.entity.User;
import com.unittestapi.unittestapi.exception.UserNotFoundException;
import com.unittestapi.unittestapi.service.UserService;
import com.unittestapi.unittestapi.web.UserApiController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(UserApiController.class)
public class UserApiControllerTests {

	private static final String END_POINT_PATH = "/users";

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserService userService;

	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		User newUser = new User();
		newUser.setEmail("");
		newUser.setFirstName("");
		newUser.setLastName("");

		String requestBody = objectMapper.writeValueAsString(newUser);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testAddShouldReturn201Created() throws Exception {
		User newUser = new User();
		String email = "essolayk@yahoo.fr";
		newUser.setId(1L);
		newUser.setEmail(email);
		newUser.setFirstName("Yannick");
		newUser.setLastName("Essola");
		newUser.setPassword("esso123");

		when(userService.add(newUser)).thenReturn(newUser);

		String requestBody = objectMapper.writeValueAsString(newUser);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(requestBody))
				.andExpect(content().contentType("application/json")).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/users/1")))
				.andExpect(jsonPath("$.email", containsString(email))).andExpect(jsonPath("$.password").doesNotExist())
				.andDo(print());
	}

	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		when(userService.get(userId)).thenThrow(UserNotFoundException.class);

		mockMvc.perform(get(requestURI)).andExpect(status().isNotFound()).andDo(print());
	}

	@Test
	public void testGetShouldReturn200Ok() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		String email = "essolayk@yahoo.fr";
		User user = new User();
		user.setId(userId);
		user.setEmail(email);
		user.setFirstName("Yannick");
		user.setLastName("Essola");
		user.setPassword("esso123");

		when(userService.get(userId)).thenReturn(user);

		mockMvc.perform(get(requestURI).contentType("application/json"))
				.andExpect(content().contentType("application/json")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", containsString(email))).andExpect(jsonPath("$.password").doesNotExist())
				.andDo(print());
	}

	@Test
	public void testListShouldReturn204NoContent() throws Exception {
		when(userService.list()).thenReturn(new ArrayList<>()); // on peut ne pas utiliser Mockito
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent()).andDo(print());
		verify(userService, times(1)).list();
	}

	@Test
	public void testListShouldReturn200Ok() throws Exception {
		when(userService.list()).thenReturn(new ArrayList<>()); // on peut ne pas utiliser Mockito

		String email = "essolayk@yahoo.fr";
		User user1 = new User();
		user1.setId(1L);
		user1.setEmail(email);
		user1.setFirstName("Yannick");
		user1.setLastName("Essola");
		user1.setPassword("esso123");

		String firstName = "Nouma";
		String lastName = "Kierane";
		User user2 = new User();
		user2.setId(2L);
		user2.setEmail("noum@yahoo.com");
		user2.setFirstName(firstName);
		user2.setLastName(lastName);
		user2.setPassword("nou123");

		List<User> listUsers = List.of(user1, user2);

		when(userService.list()).thenReturn(listUsers);

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$[0].email", containsString(email)))
				.andExpect(jsonPath("$[1].firstName", containsString(firstName)))
				.andExpect(jsonPath("$[1].lastName", containsString(lastName))).andDo(print());
		verify(userService, times(1)).list();
	}

	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		String email = "essolayk@yahoo.fr";
		User user = new User();
		user.setId(userId);
		user.setEmail(email);
		user.setFirstName("Yannick");
		user.setLastName("Essola");
		user.setPassword("esso123");

		String requestBody = objectMapper.writeValueAsString(user);

		when(userService.update(user)).thenThrow(UserNotFoundException.class);

		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
				.andExpect(status().isNotFound()).andDo(print());
	}

	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		User user = new User();
		user.setId(userId);
		user.setEmail("");
		user.setFirstName("");
		user.setLastName("");
		user.setPassword("");

		String requestBody = objectMapper.writeValueAsString(user);

		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testUpdateShouldReturn200OK() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		String email = "essolayk@yahoo.fr";
		User user = new User();
		user.setId(userId);
		user.setEmail(email);
		user.setFirstName("Yannick");
		user.setLastName("Essola");
		user.setPassword("esso123");
		
		String requestBody = objectMapper.writeValueAsString(user);

		when(userService.update(user)).thenReturn(user);
		
		mockMvc.perform(put(requestURI).contentType("application/json")
				.content(requestBody))
				.andExpect(content().contentType("application/json"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", containsString(email)))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		Mockito.doThrow(UserNotFoundException.class).when(userService).delete(userId);
		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNotFound())
				.andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		Mockito.doNothing().when(userService).delete(userId);
		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNoContent())
				.andDo(print());
	}
}
