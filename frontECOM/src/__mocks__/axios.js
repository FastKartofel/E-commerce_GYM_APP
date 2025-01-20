const mockAxios = jest.genMockFromModule('axios');

mockAxios.get = jest.fn();
mockAxios.post = jest.fn();
mockAxios.delete = jest.fn();

export default mockAxios;
