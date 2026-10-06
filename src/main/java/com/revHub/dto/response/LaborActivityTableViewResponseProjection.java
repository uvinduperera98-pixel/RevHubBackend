package com.revHub.dto.response;

import java.time.LocalDateTime;

public interface LaborActivityTableViewResponseProjection {
    Long getLaborActivityId();

    String getActivityName();

    LocalDateTime getLastModifiedDate();

    String getLastModifiedUser();

    Boolean getActive();
}
