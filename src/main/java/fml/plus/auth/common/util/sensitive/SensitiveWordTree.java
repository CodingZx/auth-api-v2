package fml.plus.auth.common.util.sensitive;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Slf4j
class SensitiveWordTree {
    private final SensitiveWordNode root;

    SensitiveWordTree() {
        // 根节点 key值啥都行
        this.root = new SensitiveWordNode((char)0,false, "", null);
    }

    public void addWord(SensitiveWord word) {
        if(word == null || Strings.isNullOrEmpty(word.getWord())) return;
        var arr = word.getWord().toCharArray();
        var node = this.root;
        for (char c : arr) {
            var nextNode = node.getNodes().get(c);
            if (nextNode == null) {
                nextNode = new SensitiveWordNode(c, false, "", node);
                node.getNodes().put(c, nextNode);
            }
            node = nextNode;
        }
        if(!node.isLeaf()) {
            node.setLeaf(true);
            node.setReplaceWord(word.getReplaceWord());
        }
    }

    public boolean search(String text) {
        var arr = text.toCharArray();
        for(var i=0; i < arr.length; i++) {
            var c = arr[i];
            // 匹配第一个节点
            var nowNode = this.root.getNodes().get(c);
            var j= i;
            while(nowNode != null) {
                if(nowNode.isLeaf()) {
                    // 已经到底 并且匹配上
                    return true;
                } else {
                    if(++j >= arr.length) break; // 已经到结尾了 并且没匹配上
                    nowNode = nowNode.getNodes().get(arr[j]);
                }
            }
        }
        return false;
    }

    public SensitiveWordResponse replaceOrThrow(String text) throws SensitiveWordException {
        var processResp = new SensitiveWordResponse();
        var matchWords = new LinkedList<String>();
        var result = new StringBuilder();
        var arr = text.toCharArray();
        for(var i=0; i < arr.length; i++) {
            var c = arr[i];
            log.debug("now char : {} index: {}", c, i);
            // 匹配第一个节点
            var nowNode = this.root.getNodes().get(c);
            var j= i;
            boolean replaceStatus = false;
            while(nowNode != null) {
                log.debug("now index j : {}", j);
                log.debug("start match....");
                if(nowNode.isLeaf()) {
                    // 已匹配
                    log.debug("leaf.....");
                    // 判断一下后面的是否还可以匹配 类似于 shab 和 shabi
                    if(j + 1 < arr.length) {
                        var next = nowNode.getNodes().get(arr[j + 1]);
                        if(next != null) {
                            // 没到底.. 优先全匹配
                            nowNode = next;
                            j ++;
                            continue;
                        }
                    }

                    // 到底了..
                    String fullWord = getFullWord(nowNode);
                    if(nowNode.isHint()) { // 触发到需要提示的敏感词 直接抛异常
                        throw new SensitiveWordException(fullWord);
                    } else { // 需要替换
                        replaceStatus = true;
                        i = j; // 跳过已匹配的敏感词

                        matchWords.add(fullWord);
                        processResp.setMark(processResp.isMark() || nowNode.isMark());
                    }
                    break;
                } else {
                    log.debug("not leaf.....");
                    if(++j >= arr.length) break; // 已经到结尾了 并且没匹配上
                    nowNode = nowNode.getNodes().get(arr[j]);
                }
            }
            log.debug("end match....");
            if(replaceStatus) {
                result.append(nowNode.getReplaceWord());
            } else {
                result.append(c);
            }
        }
        processResp.setResultText(result.toString());
        processResp.setMatchWords(matchWords);
        return processResp;
    }

    public String getFullWord(SensitiveWordNode node) {
        StringBuilder builder = new StringBuilder();
        while(node.getParent() != null) {
            builder.insert(0, node.getCharacter());
            node = node.getParent();
        }
        return builder.toString();
    }
}
