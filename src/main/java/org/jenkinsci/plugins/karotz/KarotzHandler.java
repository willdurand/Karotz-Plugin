package org.jenkinsci.plugins.karotz;

import hudson.model.AbstractBuild;

/**
 * karotz Handler
 *
 * @author Seiji Sogabe
 */
public interface KarotzHandler {

    /**
     * Triggered on build start.
     */
    void onStart(AbstractBuild<?, ?> build);

    /**
     * Triggered on build failure.
     */
    void onFailure(AbstractBuild<?, ?> build);

    /**
     * Triggered on build recover.
     */
    void onRecover(AbstractBuild<?, ?> build);

    /**
     * Triggered on build success.
     */
    void onSuccess(AbstractBuild<?, ?> build);

    /**
     * Triggered on build unstable.
     */
    void onUnstable(AbstractBuild<?, ?> build);

}
