package me.jaden.todo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.router

@Configuration
class TodoRouter(private var handler: TodoHandler) {
    @Bean
    fun routerFunction() = nest(path("/todos"),
            router {
                listOf(
                        GET("/", handler::getAll),
                        GET("/{id}", handler::getById),
                        POST("/", handler::save),
                        PUT("/{id}/done", handler::done),
                        DELETE("/{id}", handler::delete))
            }
    )
}