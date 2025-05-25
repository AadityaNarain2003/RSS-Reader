package com.sismics.reader.core.dao.file.rss;

import java.util.List;

/**
 * Generalized URL guesser for Atom feeds.
 */
public class AtomUrlGuesser {

    /**
     * Guess the correct article URL from a set of links.
     * 
     * @param atomLinkList List of links
     * @return Article URL
     */
    public String guessArticleUrl(List<AtomLink> atomLinkList) {
        if (atomLinkList == null || atomLinkList.isEmpty()) {
            return null;
        }

        for (AtomLink atomLink : atomLinkList) {
            if (atomLink.getRel() == null && atomLink.getType() == null) {
                return atomLink.getHref();
            }
        }

        for (AtomLink atomLink : atomLinkList) {
            if ("alternate".equals(atomLink.getRel()) && "text/html".equals(atomLink.getType())) {
                return atomLink.getHref();
            }
        }

        return atomLinkList.get(0).getHref();
    }

    /**
     * Guess the correct site URL from a set of links.
     * 
     * @param atomLinkList List of links
     * @return Site URL
     */
    public String guessSiteUrl(List<AtomLink> atomLinkList) {
        if (atomLinkList == null || atomLinkList.isEmpty()) {
            return null;
        }

        for (AtomLink atomLink : atomLinkList) {
            if ("alternate".equalsIgnoreCase(atomLink.getRel())) {
                return atomLink.getHref();
            }
        }

        for (AtomLink atomLink : atomLinkList) {
            if (!"self".equals(atomLink.getRel())) {
                return atomLink.getHref();
            }
        }

        return null;
    }

    /**
     * Guess the correct feed URL from a set of links.
     *
     * @param atomLinkList List of links
     * @return Feed URL
     */
    public String guessFeedUrl(List<AtomLink> atomLinkList) {
        if (atomLinkList == null || atomLinkList.isEmpty()) {
            return null;
        }

        for (AtomLink atomLink : atomLinkList) {
            if ("self".equalsIgnoreCase(atomLink.getRel())) {
                return atomLink.getHref();
            }
        }

        return null;
    }

    /**
     * Guess the correct article comment URL from a set of links.
     * 
     * @param atomLinkList List of links
     * @return Comment URL
     */
    public String guessCommentUrl(List<AtomLink> atomLinkList) {
        if (atomLinkList == null || atomLinkList.isEmpty()) {
            return null;
        }

        for (AtomLink atomLink : atomLinkList) {
            if ("replies".equals(atomLink.getRel()) && "text/html".equals(atomLink.getType())) {
                return atomLink.getHref();
            }
        }

        for (AtomLink atomLink : atomLinkList) {
            if ("replies".equals(atomLink.getRel())) {
                return atomLink.getHref();
            }
        }

        return null;
    }
}
