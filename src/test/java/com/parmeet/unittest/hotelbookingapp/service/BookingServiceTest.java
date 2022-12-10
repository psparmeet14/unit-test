package com.parmeet.unittest.hotelbookingapp.service;


import com.parmeet.unittest.hotelbookingapp.dao.BookingDAO;
import com.parmeet.unittest.hotelbookingapp.exception.BusinessException;
import com.parmeet.unittest.hotelbookingapp.external.MailSender;
import com.parmeet.unittest.hotelbookingapp.model.BookingRequest;
import com.parmeet.unittest.hotelbookingapp.model.Room;
import com.parmeet.unittest.hotelbookingapp.util.CurrencyConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private PaymentService paymentServiceMock;

    @Mock
    private RoomService roomServiceMock;

    @Spy
    private BookingDAO bookingDAOMock;

    @Mock
    private MailSender mailSenderMock;

    @Captor
    private ArgumentCaptor<Double> doubleCaptor;


    @Test
    void should_CalculateCorrectPrice_When_CorrectInput() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022, 10, 1), 2, false);
        var expectedPrice = 4 * 2 * 50.0;

        // when
        var actualPrice = bookingService.calculatePrice(bookingRequest);

        // then
        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    void should_CountAvailablePlaces() {
        // given
        int expected = 0;

        // when
        int actual = bookingService.getAvailablePlaceCount();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void should_CountAvailablePlaces_When_OneRoomAvailable() {
        // given
        when(this.roomServiceMock.getAvailableRooms())
                .thenReturn(Collections.singletonList(new Room("Room 1", 2)));
        var expected = 2;

        // when
        var actual = bookingService.getAvailablePlaceCount();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void should_CountAvailablePlaces_When_MultipleRoomsAvailable() {
        // given
        var rooms = Arrays.asList(
                new Room("Room 1", 2),
                new Room("Room 2", 5));
        when(this.roomServiceMock.getAvailableRooms()).thenReturn(rooms);
        var expected = 7;

        // when
        var actual = bookingService.getAvailablePlaceCount();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void should_CountAvailablePlaces_When_CalledMultipleTimes() {
        // given
        when(this.roomServiceMock.getAvailableRooms())
                .thenReturn(Collections.singletonList(new Room("Room 1", 5)))
                .thenReturn(Collections.emptyList());
        var expectedFirstCall = 5;
        var expectedSecondCall = 0;

        // when
        var actualFirst = bookingService.getAvailablePlaceCount();
        var actualSecond = bookingService.getAvailablePlaceCount();

        // then
        assertAll(
                () -> assertEquals(expectedFirstCall, actualFirst),
                () -> assertEquals(expectedSecondCall, actualSecond)
        );
    }

    @Test
    void should_ThrowException_When_NoRoomAvailable() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, false);
        when(this.roomServiceMock.findAvailableRoomId(bookingRequest))
                .thenThrow(BusinessException.class);

        // when
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        // then
        assertThrows(BusinessException.class, executable);
    }

    @Test
    void should_NotCompleteBooking_When_PriceTooHigh() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, true);
        when(this.paymentServiceMock.pay(any(), anyDouble()))
                .thenThrow(BusinessException.class);
        //when(this.paymentServiceMock.pay(any(), eq(400.0))).thenThrow(BusinessException.class);

        // when
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        // then
        assertThrows(BusinessException.class, executable);
    }

    @Test
    void should_InvokePayment_When_Prepaid() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, true);

        // when
        bookingService.makeBooking(bookingRequest);

        // then
        verify(this.paymentServiceMock, times(1)).pay(bookingRequest, 400.0);
        verifyNoMoreInteractions(this.paymentServiceMock);
    }

    @Test
    void should_NotInvokePayment_When_NotPrepaid() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, false);

        // when
        bookingService.makeBooking(bookingRequest);

        // then
        verify(this.paymentServiceMock, never()).pay(any(), anyDouble());
    }

    @Test
    void should_MakeBooking_When_InputOK() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, false);

        // when
        var bookingId = bookingService.makeBooking(bookingRequest);

        // then
        verify(this.bookingDAOMock).save(bookingRequest);
        System.out.println("Booking ID: " + bookingId);
    }

    @Test
    void should_CancelBooking_When_InputOK() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, true);
        bookingRequest.setRoomId("1.3");
        var bookingId = "1";

        doReturn(bookingRequest).when(bookingDAOMock).get(bookingId);

        // when
        bookingService.cancelBooking(bookingId);

        // then
    }

    @Test
    void should_ThrowException_When_MailNotReady() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, true);
        doThrow(new BusinessException()).when(mailSenderMock).sendBookingConfirmation(any());

        // when
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        // then
        assertThrows(BusinessException.class, executable);
    }

    @Test
    void should_PayCorrectPrice_When_InputOK() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, true);

        // when
        bookingService.makeBooking(bookingRequest);

        // then
        verify(paymentServiceMock, times(1)).pay(eq(bookingRequest), doubleCaptor.capture());
        double capturedValue = doubleCaptor.getValue();
        assertEquals(400.0, capturedValue);
    }

    @Test
    void should_PayCorrectPrices_When_MultipleCalls() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, true);
        var bookingRequest2 = new BookingRequest("2", LocalDate.of(2022,9,27),
                LocalDate.of(2022,9,28), 2, true);
        var expectedValues = Arrays.asList(400.0, 100.0);

        // when
        bookingService.makeBooking(bookingRequest);
        bookingService.makeBooking(bookingRequest2);

        // then
        verify(paymentServiceMock, times(2)).pay(any(), doubleCaptor.capture());
        var capturedValues = doubleCaptor.getAllValues();
        assertEquals(expectedValues, capturedValues);
    }

    @Test
    void should_CountAvailablePlaces_When_OneRoomAvailable_BDDStyle() {
        // given
        given(this.roomServiceMock.getAvailableRooms())
                .willReturn(Collections.singletonList(new Room("Room 1", 2)));
        var expected = 2;

        // when
        var actual = bookingService.getAvailablePlaceCount();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void should_InvokePayment_When_Prepaid_BDDStyle() {
        // given
        var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                LocalDate.of(2022,10,1), 2, true);

        // when
        bookingService.makeBooking(bookingRequest);

        // then
        then(this.paymentServiceMock).should(times(1)).pay(bookingRequest, 400.0);
        verifyNoMoreInteractions(this.paymentServiceMock);
    }

    @Test
    void should_CalculateCorrectPriceInEuro() {
        try (MockedStatic<CurrencyConverter> mockedConverter = mockStatic(CurrencyConverter.class)) {
            // given
            var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                    LocalDate.of(2022,10,1), 2, true);
            var expected = 400.0;
            mockedConverter.when(() -> CurrencyConverter.toEuro(anyDouble())).thenReturn(400.0);

            // when
            var actual = bookingService.calculatePriceEuro(bookingRequest);

            // then
            assertEquals(expected, actual);
        }
    }

    @Test
    void should_CalculateCorrectPriceInEuro_UsingAnswers() {
        try (MockedStatic<CurrencyConverter> mockedConverter = mockStatic(CurrencyConverter.class)) {
            //given
            var bookingRequest = new BookingRequest("1", LocalDate.of(2022,9,27),
                    LocalDate.of(2022,10,1), 2, true);
            var expected = 400.0 * 0.8;
            mockedConverter.when(() -> CurrencyConverter.toEuro(anyDouble()))
                    .thenAnswer(inv -> (double) inv.getArgument(0) * 0.8);

            // when
            var actual = bookingService.calculatePriceEuro(bookingRequest);

            // then
            assertEquals(expected, actual);
        }
    }

}