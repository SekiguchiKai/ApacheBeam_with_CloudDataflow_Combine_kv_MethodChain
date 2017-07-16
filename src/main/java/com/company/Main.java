package com.company;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.values.KV;

/**
 * メイン
 * Created by sekiguchikai on 2017/07/15.
 */
public class Main {
    /**
     * 関数オブジェクト
     * 与えられたString str, String numを","で分割し、
     * numをInteger型に変更して、KV<String, Integer>型にする
     */
    static class SplitWordsAndMakeKVFn extends DoFn<String, KV<String, Integer>> {
        @ProcessElement
        // ProcessContextは、inputを表すobject
        // 自分で定義しなくてもBeam SDKが勝手に取ってきてくれる
        public void processElement(ProcessContext c) {
            // ","で分割
            String[] words = c.element().split(",");
            // 分割したword[0]をKに、words[1]をIntegerに変換してVにする
            c.output(KV.of(words[0], Integer.parseInt(words[1])));
        }
    }


    /**
     * 関数オブジェクト
     * KV<String, Iterable<Integer>型をString型に変更する
     */
    static class TransTypeFromKVAndMakeStringFn extends DoFn<KV<String, Integer>, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            // inputをString型に変換する
            c.output(String.valueOf(c.element()));

        }

    }


    /**
     * インプットデータのパス
     */
    private static final String INPUT_FILE_PATH = "./sample.txt";
    /**
     * アウトデータのパス
     */
    private static final String COMBINE_OUTPUT_FILE_PATH = "./src/main/resources/combine_result/result.csv";

    /**
     * メイン
     * @param args 引数
     */
    public static void main(String[] args) {
        Pipeline pipeline = Pipeline.create(PipelineOptionsFactory.create());
        pipeline
                .apply(TextIO.read().from(INPUT_FILE_PATH))
                .apply(ParDo.of(new SplitWordsAndMakeKVFn()))
                .apply(Sum.integersPerKey())
                .apply(ParDo.of(new TransTypeFromKVAndMakeStringFn()))
                .apply(TextIO.write().to(COMBINE_OUTPUT_FILE_PATH));
        pipeline.run().waitUntilFinish();
    }


}