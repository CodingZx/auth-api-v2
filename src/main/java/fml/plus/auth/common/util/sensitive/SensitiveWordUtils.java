package fml.plus.auth.common.util.sensitive;

import java.util.List;

public class SensitiveWordUtils {
    private static SensitiveWordTree TREE = new SensitiveWordTree();

    /**
     * 添加敏感词
     */
    public static void addWords(List<SensitiveWord> words) {
        for(var word : words) {
            TREE.addWord(word);
        }
    }

    /**
     * 添加敏感词
     */
    public static void addWord(SensitiveWord word) {
        TREE.addWord(word);
    }

    /**
     * 搜索是否包含敏感词
     */
    public static boolean search(String text) {
        return TREE.search(text);
    }

    /**
     * 替换敏感词或抛出异常
     * @throws SensitiveWordException 需提醒为敏感词时抛出异常
     */
    public static SensitiveWordResponse replaceText(String text) throws SensitiveWordException {
        return TREE.replaceOrThrow(text);
    }

    /**
     * 重新构造敏感词树并替换
     */
    public static void replaceWords(List<SensitiveWord> words) {
        var newTree = new SensitiveWordTree();
        for(var word : words) {
            newTree.addWord(word);
        }
        TREE = newTree;
    }

}
