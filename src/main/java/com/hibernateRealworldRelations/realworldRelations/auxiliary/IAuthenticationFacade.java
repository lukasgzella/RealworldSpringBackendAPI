package com.hibernateRealworldRelations.realworldRelations.auxiliary;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
