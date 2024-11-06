package dev.langchain4j.model.chat;

import dev.langchain4j.Experimental;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.Response;

import java.util.List;
import java.util.Set;

import static dev.langchain4j.internal.Utils.isNullOrEmpty;
import static java.util.Arrays.asList;

/**
 * TODO review all javadoc in this class
 * Represents a language model that has a chat interface.
 *
 * @see StreamingChatLanguageModel
 */
public interface ChatLanguageModel {

    /**
     * TODO
     * <p>
     * A temporary default implementation of this method is necessary
     * until all {@link ChatLanguageModel} implementations adopt it. It should be removed once that occurs.
     *
     * @param chatRequest
     * @return
     */
    @Experimental
    default ChatResponse chat(ChatRequest chatRequest) {

        ResponseFormat responseFormat = chatRequest.responseFormat();
        if (responseFormat != null && responseFormat.type() == ResponseFormatType.JSON) {
            // TODO check supportedCapabilities() instead?
            throw new UnsupportedOperationException("JSON response type is not supported by this model provider");
        }

        Response<AiMessage> response;
        if (isNullOrEmpty(chatRequest.toolSpecifications())) {
            response = generate(chatRequest.messages());
        } else {
            response = generate(chatRequest.messages(), chatRequest.toolSpecifications());
        }

        return ChatResponse.builder()
            .aiMessage(response.content())
            .tokenUsage(response.tokenUsage())
            .finishReason(response.finishReason())
            .build();
    }

    @Experimental
    default String chat(String userMessage) {

        ChatRequest chatRequest = ChatRequest.builder()
            .messages(UserMessage.from(userMessage))
            .build();

        ChatResponse chatResponse = chat(chatRequest);

        return chatResponse.aiMessage().text();
    }

    @Experimental
    default Set<Capability> supportedCapabilities() {
        return Set.of();
    }

    /**
     * Generates a response from the model based on a message from a user.
     * This is a convenience method that receives the message from a user as a String
     * and returns only the generated response.
     *
     * @param userMessage The message from the user.
     * @return The response generated by the model.
     */
    default String generate(String userMessage) {
        return generate(UserMessage.from(userMessage)).content().text();
    }

    /**
     * Generates a response from the model based on a sequence of messages.
     * Typically, the sequence contains messages in the following order:
     * System (optional) - User - AI - User - AI - User ...
     *
     * @param messages An array of messages.
     * @return The response generated by the model.
     */
    default Response<AiMessage> generate(ChatMessage... messages) {
        return generate(asList(messages));
    }

    /**
     * Generates a response from the model based on a sequence of messages.
     * Typically, the sequence contains messages in the following order:
     * System (optional) - User - AI - User - AI - User ...
     *
     * @param messages A list of messages.
     * @return The response generated by the model.
     */
    Response<AiMessage> generate(List<ChatMessage> messages);

    /**
     * Generates a response from the model based on a list of messages and a list of tool specifications.
     * The response may either be a text message or a request to execute one of the specified tools.
     * Typically, the list contains messages in the following order:
     * System (optional) - User - AI - User - AI - User ...
     *
     * @param messages           A list of messages.
     * @param toolSpecifications A list of tools that the model is allowed to execute.
     *                           The model autonomously decides whether to use any of these tools.
     * @return The response generated by the model.
     * {@link AiMessage} can contain either a textual response or a request to execute one of the tools.
     */
    default Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> toolSpecifications) {
        throw new IllegalArgumentException("Tools are currently not supported by this model");
    }

    /**
     * Generates a response from the model based on a list of messages and a single tool specification.
     * The model is forced to execute the specified tool.
     * Typically, the list contains messages in the following order:
     * System (optional) - User - AI - User - AI - User ...
     *
     * @param messages          A list of messages.
     * @param toolSpecification The specification of a tool that must be executed.
     *                          The model is forced to execute this tool.
     * @return The response generated by the model.
     * {@link AiMessage} contains a request to execute the specified tool.
     */
    default Response<AiMessage> generate(List<ChatMessage> messages, ToolSpecification toolSpecification) {
        throw new IllegalArgumentException("Tools are currently not supported by this model");
    }
}
