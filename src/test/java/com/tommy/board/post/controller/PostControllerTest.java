package com.tommy.board.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tommy.board.post.domain.Post;
import com.tommy.board.post.domain.PostRepository;
import com.tommy.board.post.dto.PostSaveRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {

    private static final String URL_LOCALHOST = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    private String title;
    private String description;
    private String author;

    @BeforeEach
    void setUp() {
        title = "post title";
        description = "post description";
        author = "hangyeol";

        postRepository.deleteAll();

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("게시물 등록 Api 호출")
    void postSave() throws Exception {
        // given
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.of(title, description, author);

        String url = URL_LOCALHOST + port + "/api/posts";

        // when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(postSaveRequestDto)))
                .andExpect(status().isCreated());

        // then
        List<Post> posts = postRepository.findAll();
        Post post = posts.get(0);

        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("전체 Post 조회 Api 호출")
    void postFindAll() throws Exception {
        // given
        postRepository.save(Post.write(title, description, author));

        String url = URL_LOCALHOST + port + "/api/posts";

        // when
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        // then
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(1);
    }

    @Test
    @DisplayName("특정 Id의 Post 조회 Api 호출")
    void postFindById() throws Exception {
        // given
        postRepository.save(Post.write(title, description, author));

        String url = URL_LOCALHOST + port + "/api/posts/1";

        // when
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        // then
        Post post = postRepository.findById(1L)
                .orElseThrow(IllegalArgumentException::new);

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("post title");
    }

}
