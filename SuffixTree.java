/**
 * AUTHOR: Jah Markabawi
 * DESCRIPTION: An implementation of Ukkonen's suffix tree construction algorithm, which uses
 * a HashMap to store the character -> Node mappings rather than a char[].
 * USAGE: Provides several useful methods for text analysis (e.g., longest repeating substring)
 * DATE: 08/20/2019
 */

package algorithms;

import java.util.HashMap;
import java.util.Map;


public class SuffixTree {

    private String text;
    private SuffixTreeNode root;

    // Active Points
    private SuffixTreeNode activeNode;
    private int activeEdge = -1;
    private int activeLength = 0;

    // End value of leaves
    private IntReference leafEnd = new IntReference(-1);

    private int remainingSuffixCount = 0;


    /**
     * Constructs a suffix tree using Ukkonen's algorithm, which can then be used for
     * further string operations.
     * @param text : the string to be used in the construction
     */
    public SuffixTree(String text)
    {
        // Set the defaults
        this.text = text;
        root = new SuffixTreeNode(0, new IntReference(-1));
        activeNode = root;

        char[] data = text.toCharArray();

        // Iterate through each phase i
        for(int i = 0; i < data.length; ++i) {
            remainingSuffixCount++;

            // Rule 1
            // Add the character to all existing leaves
            leafEnd.increment();

            this.extendTree(data, i);
        }
    }

    /**
     * Method used to find the index of a substring in the processed text
     * @param pattern
     * @return the index of the first location, otherwise -1
     */
    public int findInText(String pattern) {
        return findPattern(root, pattern.toCharArray(), 0);
    }

    /**
     * Tail-recursive helper method to find a pattern or substring in the chunk of text
     * @param pattern : the char array pattern
     * @param indexInPattern : the current index of the pattern being checked in the recursive call
     * @return the index of the first location, otherwise -1
     */
    private static int findPattern(SuffixTreeNode node, char[] pattern, int indexInPattern) {
        for(char c : node.children.keySet()) {
            if(c == pattern[indexInPattern]) {
                return indexInPattern == pattern.length - 1
                        ? node.children.get(c).start
                        : findPattern(node.children.get(c), pattern, ++indexInPattern);
            }
        }
        return -1;
    }

    /**
     * Longest Repeating Substring
     * @return a string representing the longest repeating substring of the text
     */
    public String longestRepeatingSubstring() {
        SuffixTreeNode deepestNode = deepestInternalNode(root);
        return deepestNode == root
                ? ""
                : this.text.substring(0, deepestNode.end.value);
    }

    private static SuffixTreeNode deepestInternalNode (SuffixTreeNode node) {
        SuffixTreeNode maxNode = node;
        for(char c : node.children.keySet()) {
            SuffixTreeNode child = node.children.get(c);
            if(child.children.keySet().size() > 0  // If the child is also an internal node
                    && child.end.value > maxNode.end.value) // and its depth is greater than current max
            {
                maxNode = child;  // set the current deepest internal node to that child
            }
        }
        // If none of the children are internal nodes
        // Else return the deepest internal nodes of the children
        return maxNode == node ? maxNode : deepestInternalNode(maxNode);
    }

    /**
     * Method for building the suffix tree during each phase i
     * Uses active points and suffix links to rapidly traverse the tree.
     * @param data : the character array representation of the string
     * @param i : the index of the current character/phase
     */
    private void extendTree(char[] data, int i) {
        SuffixTreeNode lastCreatedNode = null;

        while(remainingSuffixCount > 0) {
            // When there is no existing build-off point or active point
            if(activeLength == 0) activeEdge = i;

            // If the current path doesn't contain the character
            // then create a new leaf for that suffix
            if( !activeNode.children.containsKey(data[activeEdge]) ) {
                // Initialize the new branch
                activeNode.children.put(data[activeEdge], new SuffixTreeNode(i, leafEnd));

                if(lastCreatedNode != null) {
                    lastCreatedNode.link = activeNode;
                    lastCreatedNode = null;
                }

            // Otherwise, get the next node and check the rules again
            } else {
                SuffixTreeNode next = activeNode.children.get(i);

                if(walkDown(next)) continue;

                if(data[i] == data[next.start + activeLength]) {
                    if(lastCreatedNode != null && activeNode != root) {
                        lastCreatedNode.link = activeNode;
                        lastCreatedNode = null;
                    }
                    activeLength++;
                    break;
                }

                // Split the existing branch into an internal node and
                // the corresponding leaf nodes
                // Internal:
                IntReference splitEnd = new IntReference(next.start + activeLength - 1);
                SuffixTreeNode split = new SuffixTreeNode(next.start, splitEnd);
                activeNode.children.put(data[activeEdge], split);
                // Newly created leaf:
                split.children.put(data[i], new SuffixTreeNode(i, leafEnd));
                // Old part of branch as leaf:
                next.start += activeLength;
                split.children.put(data[next.start], next);

                // Update suffix links if any
                if(lastCreatedNode != null) {
                    lastCreatedNode.link = split;
                }
                lastCreatedNode = split;
            }

            remainingSuffixCount--;

            if(activeNode == root && activeLength > 0) {
                activeLength--;
                activeEdge = i - remainingSuffixCount + 1;
            } else if(activeNode != root) {
                activeNode = activeNode.link;
            }
        }
    }

    /**
     * Private method for update the active point when the character is found
     * on the current path
     * @param currentNode
     * @return a boolean indicating whether walkDown could be performed
     */
    private boolean walkDown(SuffixTreeNode currentNode) {
        int edgeLength = currentNode.edgeLength();
        if(activeLength >= edgeLength) {
            activeLength -= edgeLength;
            activeEdge += edgeLength;
            activeNode = currentNode;
            return true;
        }
        else return false;
    }


    /**
     * The node class that holds suffix indices and any extending branches.
     */
    private class SuffixTreeNode
    {

        protected Map<Character, SuffixTreeNode> children;
        protected SuffixTreeNode link; // a link when this is an internal node

        // Indices of the substring/suffix
        protected int start;
        protected IntReference end;


        /**
         * One of the nodes of the suffix tree
         * @param start
         * @param end
         */
        public SuffixTreeNode(int start, IntReference end)
        {
            this.end = end;
            this.start = start;
            this.children = new HashMap<>();
        }

        public int edgeLength() {
            return this.end.value - this.start + 1;
        }

        @Override
        public String toString() {
            return text.substring(this.start, this.end.value);
        }
    }

    /**
     * For passing a reference of the current end value
     */
    private class IntReference {

        protected int value;

        /**
         * Stores an integer (value) that can be passed by reference
         * @param value
         */
        public IntReference(int value)
        {
            this.value = value;
        }

        public void increment() {
            this.value++;
        }
    }
}