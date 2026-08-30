const serverUrl = 'http://localhost:8080';

export const environment = {
  /** Origin only — image URLs come back server-relative and need this prefixed. */
  serverUrl,
  apiUrl: `${serverUrl}/api`,
};
