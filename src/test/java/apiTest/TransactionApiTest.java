package apiTest;

import com.n26.BackendChallengeApplication;
import com.n26.domain.Transaction;
import com.n26.dto.TransactionDTO;
import com.n26.repository.TransactionRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendChallengeApplication.class)
@WebAppConfiguration
public class TransactionApiTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        transactionRepository.deleteAll();
        createTransactions();
    }

    public Long getGmtTimestamp() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
    }

    public Transaction createTransaction(double amount, long timestamp) {
        return transactionRepository.save(new Transaction(amount, timestamp));
    }

    public void createTransactions() {
        createTransaction(120, getGmtTimestamp()-10000);
        createTransaction(105, getGmtTimestamp()+70000);
        createTransaction(105, getGmtTimestamp()-20000);
        createTransaction(105, getGmtTimestamp()-12000);
        createTransaction(105, getGmtTimestamp()-42000);
        createTransaction(105, getGmtTimestamp()-30000);
        createTransaction(105, getGmtTimestamp()-45000);
        createTransaction(105, getGmtTimestamp()-61000);
        createTransaction(105, getGmtTimestamp()-50000);
        createTransaction(105, getGmtTimestamp()-62000);
    }
    @Test
    public void addTransactionWithNotNullId() throws Exception {
        TransactionDTO transaction = new TransactionDTO("lkjhlkjhlkjh5454df55454","120.01", getGmtTimestamp().toString());
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(json(transaction)))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void addTransactionWithInvalidAttributesTest() throws Exception {

        TransactionDTO transaction = new TransactionDTO(null, getGmtTimestamp().toString());
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(json(transaction)))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        transaction.setAmount("11225.3");
        transaction.setTimestamp(null);
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(json(transaction)))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void addTransactionWithGoodTimestampTest() throws Exception {
        Long timestamp = getGmtTimestamp()-10000;
        TransactionDTO transaction = new TransactionDTO("15.5", timestamp.toString());
        int size = transactionRepository.findAll().size();
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(json(transaction)))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
        Assert.assertEquals(size+1, transactionRepository.findAll().size());
    }

    @Test
    public void addTransactionWithOldTimestampTest() throws Exception {
        Long timestamp = getGmtTimestamp()-100000;
        TransactionDTO transaction = new TransactionDTO("168.5", timestamp.toString());
        mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(json(transaction)))
                .andExpect(status().is(204))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getStatisticsWithoutTransactionTest() throws Exception {
        transactionRepository.deleteAll();
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", Matchers.is("there is no transactions in last 60 seconds")));
    }


    @Test
    public void getStatisticsTest() throws Exception {
        createTransaction(1255, getGmtTimestamp()-50000);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void getStatisticsTest1() throws Exception {
        createTransaction(55, getGmtTimestamp()-50000);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void getStatisticsTest2() throws Exception {
        createTransaction(155, getGmtTimestamp()-70000);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void getStatisticsTest3() throws Exception {
        createTransaction(17, getGmtTimestamp()-50000);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void getStatisticsTest4() throws Exception {
        createTransaction(180, getGmtTimestamp()-50000);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void getStatisticsTest5() throws Exception {
        createTransaction(180, getGmtTimestamp()-61000);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
    @Test
    public void getStatisticsTest6() throws Exception {
        createTransaction(180, getGmtTimestamp()-59000);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
