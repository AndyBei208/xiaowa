package com.xiaowa.writingassistant.ai;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class AIClientImpl implements AIClient {

    /**
     * Stub 实现：当前返回空列表，或者抛异常提示尚未实现
     */
    @Override
    public List<CharacterSetting> extractCharacterSettings(String content) {
        // TODO: 调用真正的 AI 接口并解析返回结果
        return Collections.emptyList();
        // 或者：
        // throw new UnsupportedOperationException("AI extraction not implemented yet");
    }
}