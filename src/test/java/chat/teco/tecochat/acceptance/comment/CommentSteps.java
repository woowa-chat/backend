package chat.teco.tecochat.acceptance.comment;

import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.given;
import static chat.teco.tecochat.acceptance.util.JsonMapper.toJson;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.comment.application.usecase.QueryAllCommentByChatIdUseCase.CommentQueryDto;
import chat.teco.tecochat.comment.presentation.request.UpdateCommentRequest;
import chat.teco.tecochat.comment.presentation.request.WriteCommentRequest;
import com.jayway.jsonpath.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CommentSteps {

    public static ExtractableResponse<Response> 댓글_작성_요청(String 크루명, Long 채팅_ID, String 내용) {
        WriteCommentRequest request = new WriteCommentRequest(채팅_ID, 내용);
        return given(크루명)
                .body(toJson(request))
                .when()
                .post("/comments")
                .then()
                .log().all()
                .extract();
    }

    public static Long 댓글_작성후_댓글_ID_반환(String 크루명, Long 채팅_ID, String 내용) {
        WriteCommentRequest request = new WriteCommentRequest(채팅_ID, 내용);
        var 응답 = given(크루명)
                .body(toJson(request))
                .when()
                .post("/comments")
                .then()
                .log().all()
                .extract();
        return 생성된_댓글의_ID(응답);
    }

    public static Long 생성된_댓글의_ID(ExtractableResponse<Response> 응답) {
        String location = 응답.header("location");
        String id = location.substring(location.lastIndexOf("/") + 1);
        return Long.parseLong(id);
    }

    public static ExtractableResponse<Response> 댓글_수정_요청(
            String 크루명,
            Long 댓글_ID,
            String 내용
    ) {
        UpdateCommentRequest request = new UpdateCommentRequest(내용);
        return given(크루명).body(toJson(request))
                .when()
                .patch("/comments/{id}", 댓글_ID)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_제거_요청(String 크루명, Long 댓글_ID) {
        return given(크루명)
                .when()
                .delete("/comments/{id}", 댓글_ID)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 채팅에_달린_댓글들_조회_요청(Long 채팅_ID) {
        return given()
                .when()
                .get("/comments?chatId=" + 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static void 댓글들_조회_내용_검증(
            ExtractableResponse<Response> 응답,
            List<CommentQueryDto> 예상_결과
    ) {
        List<CommentQueryDto> 내용들 = 응답.as(new TypeRef<List<CommentQueryDto>>() {
        }.getType());
        assertThat(내용들).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_결과);
    }
}
