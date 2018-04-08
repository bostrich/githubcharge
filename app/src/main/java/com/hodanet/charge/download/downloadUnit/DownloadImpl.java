package com.hodanet.charge.download.downloadUnit;


import com.hodanet.charge.download.DownloadBean;
import com.hodanet.charge.download.feedback.DownloadFeedbackImpl;

/**
 *
 */

public interface DownloadImpl {
    void download(DownloadBean bean);
    void stop();
    void setDownloadFeedback(DownloadFeedbackImpl feedback);
    int getExitStrategy();
}
