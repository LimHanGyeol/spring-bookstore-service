package com.tommy.board.post.domain;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        testEntityManager.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Post 저장")
    void save() {
        // given
        Post post = newInstance();

        // when
        Post savedPost = testEntityManager.persist(post);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost).isEqualTo(post);
    }

    @Test
    @Order(2)
    @DisplayName("특정 Id의 Post 조회")
    void findById() {
        // given
        testEntityManager.persist(newInstance());

        // when
        Post savedPost = postRepository.findById(2L)
                .orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isEqualTo(2L);
        assertThat(savedPost.getTitle()).isEqualTo("title");
    }

    @Test
    @Order(3)
    @DisplayName("전체 Post 조회")
    void findAll() {
        // given
        Post post = newInstance();
        testEntityManager.persist(post);

        // when
        List<Post> posts = postRepository.findAll();

        // then
        assertThat(posts).hasSize(1);
    }

    private Post newInstance() {
        return Post.write("title", "description", "hangyeol");
    }
}
