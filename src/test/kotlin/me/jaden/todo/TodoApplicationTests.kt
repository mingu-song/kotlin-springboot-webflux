package me.jaden.todo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.*

@WebFluxTest
class TodoApplicationTests {

    @MockBean
    lateinit var repo: TodoRepository
    lateinit var webClient: WebTestClient
    lateinit var todo1: Todo
    lateinit var todo2: Todo

    @BeforeEach
    fun setUp() {
        todo1 = Todo().apply {
            id = 1L
            content = "this is content 1" }
        todo2 = Todo().apply {
            id = 2L
            content = "this is content 2" }
        val routerFunction = TodoRouter(TodoHandler(repo)).routerFunction()
        webClient = WebTestClient.bindToRouterFunction(routerFunction).build()
    }

    @Test
    @Throws(Exception::class)
    fun `test should return a list of todo`() {
        given(repo.findAll()).willReturn(listOf(todo1, todo2))
        val responseBody: List<Todo>? = webClient.get().uri("/todos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Todo::class.java)
                .hasSize(2)
                .returnResult().responseBody
        assertThat(responseBody?.get(1)?.id).isEqualTo(2L)
        assertThat(responseBody?.get(1)?.content).isEqualTo("this is content 2")
    }

    @Test
    @Throws(Exception::class)
    fun `test should return an item of todo by id`() {
        // given
        given(repo.findById(1L)).willReturn(Optional.of(todo1))

        // when
        val responseBody: Todo? = webClient.get().uri("/todos/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(Todo::class.java)
                .returnResult().responseBody

        // then
        assertThat(responseBody?.content).isEqualTo("this is content 1")
    }
}
