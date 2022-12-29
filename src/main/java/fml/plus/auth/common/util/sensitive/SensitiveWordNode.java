package fml.plus.auth.common.util.sensitive;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
class SensitiveWordNode {
    private char character; // 当前节点存储的字
    private boolean leaf; // 是否为叶节点
    private String replaceWord; // 替换文字
    private boolean hint; // 是否直接返回提示
    private boolean mark;  // 是否标记
    private Map<Character, SensitiveWordNode> nodes = new HashMap<>();
    private SensitiveWordNode parent; // 父级节点

    SensitiveWordNode(char character, boolean leaf, String replaceWord, SensitiveWordNode parent) {
        this.leaf = leaf;
        this.replaceWord = replaceWord;
        this.parent = parent;
        this.character = character;
    }
}
