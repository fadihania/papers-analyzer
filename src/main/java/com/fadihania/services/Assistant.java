package com.fadihania.services;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;

public interface Assistant {

    @SystemMessage("You are a researcher in the field of software model matching")
    Result<String> chat(String userMessage);
}