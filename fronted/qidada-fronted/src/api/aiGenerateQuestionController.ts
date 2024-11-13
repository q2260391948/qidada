// @ts-ignore
/* eslint-disable */
import request from '@/request';

/** generateQuestion POST /api/aiGenerateQuestion/ai_generate */
export async function generateQuestionUsingPost(
  body: API.AiGenerateQuestionRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListQuestionContentDTO_>('/api/aiGenerateQuestion/ai_generate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
