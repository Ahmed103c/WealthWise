import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-chatbot',
  imports: [FormsModule, CommonModule],
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.scss',
})
export class ChatbotComponent {
  constructor(private authservice: AuthService) {}

  question: string = '';
  history: { question: string; response: string; isloading: boolean }[] = [];
  onWelcome: boolean = true;

  sendQuestion() {
    this.onWelcome = false;
    const newMessage = {
      question: this.question,
      response: '',
      isloading: true,
    };
    this.history.push(newMessage);

    const messageIndex = this.history.length - 1;

    this.authservice.getAnswer(this.question).subscribe(
      (data) => {
        this.history[messageIndex].response = data.response;
        this.history[messageIndex].isloading = false;
      },
      (error) => {
        console.error('Erreur', error);
        this.history[messageIndex].response = 'Une erreur est survenue.';
        this.history[messageIndex].isloading = false;
      }
    );
    this.question = '';
  }
}
