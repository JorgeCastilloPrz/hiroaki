package me.jorgecastillo.hiroaki

import me.jorgecastillo.hiroaki.internal.MockServerSuite
import me.jorgecastillo.hiroaki.models.success
import me.jorgecastillo.hiroaki.services.SomeService
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(MockitoJUnitRunner::class)
class VerificationTest : MockServerSuite() {

    private lateinit var service: SomeService

    @Before
    override fun setup() {
        super.setup()
        service = server.retrofitService(GsonConverterFactory.create())
    }

    @Test
    fun matchesQueryParamsLists() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.addNewsTags(listOf("1", "2", "3"), listOf("some-tag-1", "some-tag-2", "some-tag-3"))
                .execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "i" to "1",
                        "i" to "2",
                        "i" to "3",
                        "a" to "some-tag-1",
                        "a" to "some-tag-2",
                        "a" to "some-tag-3"))
    }

    @Test
    fun matchesQueryParamsList() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.addNewsTags(listOf("1", "2", "3"), listOf("some-tag-1", "some-tag-2", "some-tag-3"))
                .execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "a" to "some-tag-1",
                        "a" to "some-tag-2",
                        "a" to "some-tag-3"))
    }

    @Test(expected = AssertionError::class)
    fun failsQueryParamListWithWrongValue() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.addNewsTags(listOf("1", "2", "3"), listOf("some-tag-1", "some-tag-2", "some-tag-3"))
                .execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "a" to "some-tag-1",
                        "a" to "some-tag-6-fail",
                        "a" to "some-tag-3"))
    }

    @Test
    fun supportsSpecialCharactersInQueryParamValues() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.addNewsTags(listOf("1", "2", "3"), listOf("user/-/tag", "user/-/tag-2"))
                .execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "i" to "1",
                        "i" to "2",
                        "i" to "3",
                        "a" to "user/-/tag",
                        "a" to "user/-/tag-2"))
    }

    @Test(expected = AssertionError::class)
    fun reportsErrorWhenMissingExpectedQueryParamWithParamList() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.getNewsByIds(listOf("1", "2")).execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "id" to "1",
                        "id" to "2",
                        "id" to "3"))
    }

    @Test(expected = AssertionError::class)
    fun reportsErrorWhenMissingExpectedQueryParam() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.getNew("1").execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "id" to "1",
                        "id" to "2"))
    }

    @Ignore @Test(expected = AssertionError::class)
    fun reportsErrorWhenMoreParametersThanTheExpectedOnesAreSent() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.getNewsByIds(listOf("1", "2", "3")).execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "id" to "1",
                        "id" to "2"))
    }
}
