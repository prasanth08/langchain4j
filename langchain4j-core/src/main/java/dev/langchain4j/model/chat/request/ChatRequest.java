package dev.langchain4j.model.chat.request;

import dev.langchain4j.Experimental;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.langchain4j.internal.Utils.copyIfNotNull;
import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;
import static java.util.Arrays.asList;

@Experimental
public class ChatRequest {

    private final List<ChatMessage> messages;
    private final List<ToolSpecification> toolSpecifications;
    private final ToolMode toolMode; // TODO wrap into "tools" pojo?
    private final ResponseFormat responseFormat;

    private ChatRequest(Builder builder) {
        this.messages = new ArrayList<>(ensureNotEmpty(builder.messages, "messages"));
        this.toolSpecifications = copyIfNotNull(builder.toolSpecifications);
        this.toolMode = builder.toolMode; // TODO set AUTO by default? only if toolSpecifications are present?
        this.responseFormat = builder.responseFormat;
    }

    public List<ChatMessage> messages() {
        return messages;
    }

    public List<ToolSpecification> toolSpecifications() {
        return toolSpecifications;
    }

    public ToolMode toolMode() {
        return toolMode;
    }

    public ResponseFormat responseFormat() {
        return responseFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRequest that = (ChatRequest) o;
        return Objects.equals(this.messages, that.messages)
            && Objects.equals(this.toolSpecifications, that.toolSpecifications)
            && Objects.equals(this.toolMode, that.toolMode)
            && Objects.equals(this.responseFormat, that.responseFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messages, toolSpecifications, toolMode, responseFormat);
    }

    @Override
    public String toString() {
        return "ChatRequest {" +
            " messages = " + messages +
            ", toolSpecifications = " + toolSpecifications +
            ", toolMode = " + toolMode +
            ", responseFormat = " + responseFormat +
            " }";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<ChatMessage> messages;
        private List<ToolSpecification> toolSpecifications;
        private ToolMode toolMode;
        private ResponseFormat responseFormat;

        public Builder messages(List<ChatMessage> messages) {
            this.messages = messages;
            return this;
        }

        public Builder messages(ChatMessage... messages) {
            return messages(asList(messages));
        }

        public Builder toolSpecifications(List<ToolSpecification> toolSpecifications) {
            this.toolSpecifications = toolSpecifications;
            return this;
        }

        public Builder toolSpecifications(ToolSpecification... toolSpecifications) {
            return toolSpecifications(asList(toolSpecifications));
        }

        public Builder toolMode(ToolMode toolMode) {
            this.toolMode = toolMode;
            return this;
        }

        public Builder responseFormat(ResponseFormat responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        // TODO consider adding responseFormat(JsonSchema) or jsonSchema(JsonSchema)

        public ChatRequest build() {
            return new ChatRequest(this);
        }
    }

    /**
     * TODO
     *
     * @param userMessage
     * @return
     */
    public static ChatRequest from(String userMessage) { // TODO needed? keep this method instead of *ChatModel.chat(String, ?)
        return ChatRequest.builder()
            .messages(UserMessage.from(userMessage))
            .build();
    }
}
