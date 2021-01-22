package inspection.management.workplace;

public class GetApiParameter {
    private String apiUrl;
    private String baseUrl;
    public GetApiParameter() {
        //this.apiUrl = "https://psi.legalbreakup.com/Api/";
        this.apiUrl = "http://demo.mywim.nl/Api/";
        //this.baseUrl = "http://cheeza.witorbit.net/";
        //this.baseUrl = "http://demo.mywim.nl/";
    }
    public String getApiUrl() {
        return apiUrl;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
}
